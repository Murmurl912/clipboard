package com.clipboard.clipboard_store.service;

import com.clipboard.clipboard_store.event.ClipboardContentEvent;
import com.clipboard.clipboard_store.repository.ContentRepository;
import com.mongodb.internal.connection.Time;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import com.clipboard.clipboard_store.repository.entity.ClipboardContent;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;

import static com.clipboard.clipboard_store.repository.entity.ClipboardContent.ContentState;
import static com.clipboard.clipboard_store.event.ClipboardContentEvent.ClipboardContentEventType.*;

@Service
public class ClipboardService {
    private final ReactiveMongoTemplate template;
    private final ContentRepository repository;
    private final MessageDigest digest;
    private final ApplicationEventPublisher publisher;
    public ClipboardService(ReactiveMongoTemplate template,
                            ContentRepository repository,
                            MessageDigest digest,
                            ApplicationEventPublisher publisher) {
        this.template = template;
        this.repository = repository;
        this.digest = digest;
        this.publisher = publisher;
    }

    public Flux<ClipboardContent> gets(String account) {
        return template.query(ClipboardContent.class)
                .matching(
                        Criteria.where("account")
                                .is(account)
                                .and("state")
                                .is(ContentState.CONTENT_STATE_NORMAL.STATE)
                ).all();

    }

    public Flux<ClipboardContent> gets(String account,
                                       Boolean star) {
        return template.query(ClipboardContent.class)
                .matching(
                        Criteria.where("account")
                                .is(account)
                                .and("star")
                                .is(star)
                                .and("state")
                                .is(ContentState.CONTENT_STATE_NORMAL.STATE)
                ).all();

    }

    public Mono<ClipboardContent> get(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException()))
                .handle((content, sink) -> {
                    if(content.state == ContentState.CONTENT_STATE_DELETE.STATE) {
                        sink.error(new RuntimeException());
                    } else {
                        sink.next(content);
                    }
                });
    }

    public Mono<ClipboardContent> star(String id,
                                       boolean star,
                                       Timestamp timestamp) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException()))
                .handle((content, sink) -> {
                    if(content.starVersion.after(timestamp)) {
                        sink.error(new RuntimeException());
                    } else {
                        sink.next(content);
                    }
                })
                .cast(ClipboardContent.class)
                .flatMap(content -> {
                    if(!content.star.equals(star)) {
                        content.star = star;
                        content.starVersion = timestamp;
                        // overall version changed
                        content.update = new Timestamp(System.currentTimeMillis());
                        return repository.save(content).map(c -> {
                            ClipboardContentEvent event =
                                    new ClipboardContentEvent(
                                            c.id,
                                            c.account,
                                            c.star ? CONTENT_STAR_EVENT : CONTENT_UNSTAR_EVENT,
                                            c.starVersion);
                            publisher.publishEvent(event);
                            return c;
                        });
                    } else {
                        return Mono.just(content);
                    }
                });
    }

    public Mono<ClipboardContent> state(String id,
                                        ContentState state,
                                        Timestamp timestamp) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException()))
                .handle((content, sink) -> {
                    if(content.stateVersion.after(timestamp)) {
                        sink.error(new RuntimeException());
                    } else {
                        sink.next(content);
                    }
                })
                .cast(ClipboardContent.class)
                .handle((content, sink) -> {
                    if(content.state == ContentState.CONTENT_STATE_DELETE.STATE) {
                        sink.error(new RuntimeException());
                    } else {
                        sink.next(content);
                    }
                })
                .cast(ClipboardContent.class)
                .flatMap(content -> {
                    if(content.state == state.STATE) {
                        return Mono.just(content);
                    }
                    content.state = state.STATE;
                    content.stateVersion = new Timestamp(System.currentTimeMillis());
                    // overall version changed
                    content.update = new Timestamp(System.currentTimeMillis());
                    return repository.save(content).map(c -> {
                        ClipboardContentEvent event =
                                new ClipboardContentEvent(
                                        c.id,
                                        c.account,
                                        CONTENT_DELETE_EVENT,
                                        c.stateVersion);
                        publisher.publishEvent(event);
                        return c;
                    });
                });
    }

    public Mono<ClipboardContent> content(String id,
                                          String text,
                                          Timestamp timestamp) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException()))
                .handle((content, sink) -> {
                    if(content.contentVersion.after(timestamp)) {
                        sink.error(new RuntimeException());
                    } else {
                        sink.next(content);
                    }
                })
                .cast(ClipboardContent.class)
                .flatMap(content -> {
                    if(content.content.equals(text)) {
                        return Mono.just(content);
                    }
                    content.content = text;
                    // overall version changed
                    content.update = new Timestamp(System.currentTimeMillis());
                    content.contentVersion = new Timestamp(System.currentTimeMillis());


                    return repository.save(content).map(c -> {
                        ClipboardContentEvent event =
                                new ClipboardContentEvent(
                                        c.id,
                                        c.account,
                                        CONTENT_UPDATE_EVENT,
                                        c.contentVersion);
                        publisher.publishEvent(event);
                        return c;
                    });
                });
    }

    public Mono<ClipboardContent> create(String account, ClipboardContent data) {
        return Mono.fromCallable(()-> hash(data.content.getBytes(StandardCharsets.UTF_8)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap((repository::findFirstByHashEquals))
                .switchIfEmpty(Mono.just(new ClipboardContent()))
                .flatMap(content -> {
                   if(StringUtils.isEmpty(content.id)) {
                       // not exist in current database
                       data.hash = hash(data.content.getBytes(StandardCharsets.UTF_8));
                       data.account = account;
                       return repository.save(data);
                   } else {
                       // exist in current database
                       content.state = ContentState.CONTENT_STATE_NORMAL.STATE;
                       content.stateVersion = new Timestamp(System.currentTimeMillis());
                       content.contentVersion = data.contentVersion;
                       // overall version changed
                       content.update = new Timestamp(System.currentTimeMillis());
                       return repository.save(data);
                   }
                })
                .map(c -> {
                    ClipboardContentEvent event =
                            new ClipboardContentEvent(
                                    c.id,
                                    c.account,
                                    CONTENT_CREATE_EVENT,
                                    c.contentVersion);
                    publisher.publishEvent(event);
                    return c;
                });
    }


    private byte[] hash(byte[] bytes) {
        return digest.digest(bytes);
    }
}

