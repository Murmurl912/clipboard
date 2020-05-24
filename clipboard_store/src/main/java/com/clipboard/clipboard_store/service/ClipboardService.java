package com.clipboard.clipboard_store.service;

import com.clipboard.clipboard_store.event.ClipboardContentEvent;
import com.clipboard.clipboard_store.repository.ContentRepository;
import com.clipboard.clipboard_store.repository.entity.ClipboardContent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

import static com.clipboard.clipboard_store.event.ClipboardContentEvent.ClipboardContentEventType.CONTENT_CREATE_EVENT;
import static com.clipboard.clipboard_store.event.ClipboardContentEvent.ClipboardContentEventType.CONTENT_STATE_EVENT;
import static com.clipboard.clipboard_store.repository.entity.ClipboardContent.ContentState;

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

    public Flux<ClipboardContent> versions(String account) {
        return repository
                .findAllByAccountEquals(account);
    }

    public Flux<ClipboardContent> version(String content) {
        return repository.findFirstByIdEquals(content);
    }

    public Flux<ClipboardContent> gets(String account) {
        return template.query(ClipboardContent.class)
                .matching(
                        Criteria.where("account")
                                .is(account)
                                .and("state")
                                .is(ContentState.CONTENT_STATE_NORMAL.STATE)
                ).all().map(content -> {
                    System.out.println(content);
                    return content;
                });

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

    public Mono<ClipboardContent> create(String account,
                                         ClipboardContent data) {
        return Mono.fromCallable(()-> hash(data.content.getBytes(StandardCharsets.UTF_8)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(hash -> repository.findFirstByHashEqualsAndAccountEquals(hash, account))
                .switchIfEmpty(Mono.just(new ClipboardContent()))
                .flatMap(content -> {
                   if(StringUtils.isEmpty(content.id)) {
                       // not exist in current database
                       data.hash = hash(data.content.getBytes(StandardCharsets.UTF_8));
                       data.account = account;
                       data.state = ContentState.CONTENT_STATE_NORMAL.STATE;
                       return repository.save(data);
                   } else {
                       if(content.update.after(data.update)) {
                           return Mono.just(content);
                       }

                       // exist in current database
                       content.state = ContentState.CONTENT_STATE_NORMAL.STATE;
                       // overall version changed
                       content.update = data.update;
                       return repository.save(content);
                   }
                })
                .map(c -> {
                    ClipboardContentEvent event =
                            new ClipboardContentEvent(c.id, account, CONTENT_CREATE_EVENT, c.update);
                    publisher.publishEvent(event);
                    return c;
                });
    }

    public Mono<ClipboardContent> delete(String account, String id, Date time) {
        return repository.findById(id)
                .flatMap(content -> {
                    // last write win
                    if(content.update.after(time)) {
                        return Mono.empty();
                    }

                    content.state = ContentState.CONTENT_STATE_DELETE.STATE;
                    return repository.save(content);
                }).map(c -> {
                    ClipboardContentEvent event =
                            new ClipboardContentEvent(c.id, account, CONTENT_STATE_EVENT, c.update);
                    publisher.publishEvent(event);
                    return c;
                });
    }


    private byte[] hash(byte[] bytes) {
        return digest.digest(bytes);
    }
}

