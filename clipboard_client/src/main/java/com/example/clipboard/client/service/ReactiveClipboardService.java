package com.example.clipboard.client.service;

import com.example.clipboard.client.lifecycle.event.content.ContentCreateEvent;
import com.example.clipboard.client.lifecycle.event.content.ContentStarEvent;
import com.example.clipboard.client.lifecycle.event.content.ContentStateEvent;
import com.example.clipboard.client.lifecycle.event.content.ContentUpdateEvent;
import com.example.clipboard.client.repository.CachedContentRepository;
import com.example.clipboard.client.repository.RemoteContentRepository;
import com.example.clipboard.client.repository.entity.Content;
import org.reactivestreams.Publisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Service
public class ReactiveClipboardService {

    private final RemoteContentRepository remote;
    private final CachedContentRepository cached;
    private final ApplicationEventPublisher publisher;
    private final MessageDigest digest;
    private String account;

    public ReactiveClipboardService(RemoteContentRepository remote,
                                    CachedContentRepository cached,
                                    ApplicationEventPublisher publisher,
                                    MessageDigest digest) {

        this.remote = remote;
        this.cached = cached;
        this.publisher = publisher;
        this.digest = digest;
    }

    public Mono<Content> create(String text) {
        return Mono.fromCallable(()-> hash(text))
                .map(cached::findContentByHashEquals)
                .map(optional -> {
                    Content content = null;
                    if(optional.isEmpty()) {
                        String uuid = UUID.randomUUID().toString();
                        content = createContent(text, uuid, getAccount());
                        content.uuid = uuid;
                    } else {
                        content = optional.get();
                        if(Objects.equals(content.content, text)) {
                            content.contentVersion = new Date();
                            content.starVersion = new Date();
                            content.state = Content.ContentState.CONTENT_STATE_NORMAL.STATE;
                            content.stateVersion = new Date();
                            content.update = new Date();
                        } else {
                            content = createContent(text, UUID.randomUUID().toString(), getAccount());
                        }
                    }
                    return content;
                })
                .map(cached::save)
                .map(content -> {
                    return content;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Content> star(String id, boolean star) {
        return Mono.fromCallable(()-> cached.findContentByIdEquals(id))
                .handle((optional, sink) -> {
                    optional.ifPresent(sink::next);
                })
                .cast(Content.class)
                .map(content -> {
                    content.star = star;
                    content.update = new Date();
                    content.starVersion = new Date();
                    return content;
                })
                .map(cached::save)
                .map(content -> {
                    return content;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Content> state(String id, Content.ContentState state) {
        return Mono.fromCallable(()-> cached.findContentByIdEquals(id))
                .handle((optional, sink) -> {
                    optional.ifPresent(sink::next);
                })
                .cast(Content.class)
                .map(content -> {
                    content.state = state.STATE;
                    content.update = new Date();
                    content.stateVersion = new Date();
                    return content;
                })
                .map(cached::save)
                .map(content -> {
                    return content;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Object> clear() {
        return Mono
                .fromRunnable(cached::deleteAll)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<Content> clipboard() {
        return Mono
                .fromCallable(() -> cached
                        .getContentsByStateEqualsOrderByUpdateDesc(Content.ContentState.CONTENT_STATE_NORMAL.STATE)
                )
                .flatMapIterable((contents)->contents);
    }

    public Flux<Content> star(boolean star) {
        return Mono
                .fromCallable(() -> cached
                        .getContentsByStateEqualsAndStarEqualsOrderByUpdateDesc(
                                Content.ContentState.CONTENT_STATE_NORMAL.STATE, star)
                )
                .flatMapIterable((contents)->contents);
    }

    private String getAccount() {
        return account;
    }

    private byte[] hash(@NonNull String str) {
        return digest.digest(str.getBytes(StandardCharsets.UTF_8));
    }

    @NotNull
    private Content createContent(@NotNull String text, String id, String account) {
        Content model = new Content();
        model.id = id;
        model.account = account;
        model.star = false;
        model.content = text;
        model.state = Content.ContentState.CONTENT_STATE_NORMAL.STATE;
        model.starVersion =
                model.contentVersion
                        = model.stateVersion
                        = model.create
                        = model.update = new Date();
        model.status = Content.ContentStatus.CONTENT_STATUS_LOCAL.STATUS;
        model.update = new Date();
        model.create = new Date();
        return model;
    }

}
