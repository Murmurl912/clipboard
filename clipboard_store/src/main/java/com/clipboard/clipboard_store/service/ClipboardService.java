package com.clipboard.clipboard_store.service;

import com.clipboard.clipboard_store.repository.ContentRepository;
import com.mongodb.internal.connection.Time;
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

@Service
public class ClipboardService {
    private final ReactiveMongoTemplate template;
    private final ContentRepository repository;
    private final MessageDigest digest;
    public ClipboardService(ReactiveMongoTemplate template,
                            ContentRepository repository,
                            MessageDigest digest) {
        this.template = template;
        this.repository = repository;
        this.digest = digest;
    }

    public Flux<ClipboardContent> gets(String account, ContentState state) {
        return template.query(ClipboardContent.class)
                .matching(
                        Criteria.where("account")
                                .is(account)
                                .and("state")
                                .is(ContentState.CONTENT_STATE_NORMAL.STATE)
                ).all();

    }

    public Mono<ClipboardContent> get(String id) {
        return repository.findById(id);
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
                        content.update = new Timestamp(System.currentTimeMillis());
                        return repository.save(content);
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
                    content.update = new Timestamp(System.currentTimeMillis());
                    return repository.save(content);
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
                    content.update = new Timestamp(System.currentTimeMillis());
                    return repository.save(content);
                });
    }

    public Mono<ClipboardContent> create(ClipboardContent data) {
        return Mono.fromCallable(()-> hash(data.content.getBytes(StandardCharsets.UTF_8)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(repository::findFirstByHashEquals)
                .switchIfEmpty(Mono.empty());
    }

    private void createNew(ClipboardContent data) {

    }

    private byte[] hash(byte[] bytes) {
        return digest.digest(bytes);
    }
}

