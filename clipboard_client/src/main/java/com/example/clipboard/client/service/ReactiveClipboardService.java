package com.example.clipboard.client.service;

import com.example.clipboard.client.repository.CachedContentRepository;
import com.example.clipboard.client.repository.RemoteContentRepository;
import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.service.worker.event.ClipboardEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class ReactiveClipboardService implements ApplicationListener<ClipboardEvent> {

    private final CachedContentRepository cached;
    private final MessageDigest digest;
    private final FluxProcessor<Content, Content> processor;
    private final FluxSink<Content> sink;
    private final Flux<Content> publisher;
    private final int historyCount = 100;
    private ApplicationContext context;

    public ReactiveClipboardService(CachedContentRepository cached,
                                    MessageDigest digest,
                                    ApplicationContext context) {
        EmitterProcessor<Content> clipboardProcessor = EmitterProcessor.create();
        this.cached = cached;
        this.digest = digest;
        this.context = context;
        processor = ReplayProcessor.create(historyCount);
        sink = processor.sink();
        publisher = processor.share();
    }

    public Mono<Content> create(String text) {
        return Mono.fromCallable(()-> hash(text))
                .map(cached::findContentByHashEquals)
                .map(optional -> {
                    Content content = null;
                    if(optional.isEmpty()) {
                        String uuid = UUID.randomUUID().toString();
                        content = createContent(text, uuid, null);
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
                            content = createContent(text, UUID.randomUUID().toString(), null);
                        }
                    }
                    return content;
                })
                .map(cached::save)
                .map(content -> {
                    submit(content);
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
                    submit(content);
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
                    submit(content);
                    return content;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Object> clear() {
        return Mono
                .fromRunnable(cached::deleteAll)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<Content> subscribe() {
        return publisher;
    }

    protected void submit(Content content) {
        sink.next(content);
    }

    protected Flux<Content> clipboard() {
        // fetch data from remote remote

        // read from local database
        return Mono
                .fromCallable(() -> {
//                    remote.contents()
//                            .subscribeOn(Schedulers.boundedElastic())
//                            .onErrorMap(error -> null)
//                            .subscribe(this::submit);
                    return cached.getContentsByStateEqualsOrderByUpdateDesc(
                            Content.ContentState.CONTENT_STATE_NORMAL.STATE);
                })
                .map(list -> {
                    System.out.println(list);
                    return list;
                })
                .flatMapIterable((contents)->contents)
                .map(c -> {
                    System.out.println(c);
                    return c;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public void refresh() {
        clipboard().subscribe(sink::next);
    }

    @Override
    public void onApplicationEvent(ClipboardEvent event) {
        switch (event.getType()) {
            case CLIPBOARD_CREATE:
                break;
            case CLIPBOARD_UPDATE:
                break;
            case CLIPBOARD_CHECK:
                break;
            case CLIPBOARD_REPORT:
                String content = (String)event.getPayloud().get("clipboard");
                create(content).subscribe();
                break;
        }
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
