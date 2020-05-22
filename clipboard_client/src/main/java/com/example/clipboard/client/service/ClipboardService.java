package com.example.clipboard.client.service;

import com.example.clipboard.client.repository.CachedContentRepository;
import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.repository.model.ContentModel;
import com.example.clipboard.client.service.worker.ClipboardEventModel;
import com.example.clipboard.client.service.worker.event.ClipboardEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClipboardService implements ApplicationListener<ClipboardEvent> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final CachedContentRepository cached;
    private final MessageDigest digest;
    private final FluxSink<Content> sink;
    private final Flux<Content> publisher;
    private final WebClient client;
    private final AppContext context;

    public ClipboardService(CachedContentRepository cached,
                            MessageDigest digest,
                            AppContext context,
                            WebClient.Builder builder) {
        EmitterProcessor<Content> clipboardProcessor = EmitterProcessor.create();
        this.cached = cached;
        this.digest = digest;
        this.context = context;
        client = builder.baseUrl(context.baseUrl).build();
        FluxProcessor<Content, Content> processor = ReplayProcessor.create(context.limit);
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
                        content = createContent(text, uuid, context.account);
                        content.uuid = uuid;
                        content.hash = hash(content.content);
                    } else {
                        content = optional.get();
                        if(Objects.equals(content.content, text)) {
                            content.state = Content.ContentState.CONTENT_STATE_NORMAL.STATE;
                            content.update = new Date();
                        } else {
                            content = createContent(text, UUID.randomUUID().toString(), null);
                        }
                    }
                    content.status = Content.ContentStatus.CONTENT_STATUS_LOCAL.STATUS;
                    return content;
                })
                .map(cached::save)
                .map(content -> {
                    logger.info("Create Content: " + content);
                    submit(content);
                    syncCreate(content, context.auto);
                    return content;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Content> delete(String id) {
        return Mono.fromCallable(()-> cached.findContentByIdEquals(id))
                .handle((optional, sink) -> {
                    optional.ifPresent(sink::next);
                })
                .cast(Content.class)
                .map(content -> {
                    if(content.status ==
                            Content.ContentStatus.CONTENT_STATUS_LOCAL.STATUS) {
                        cached.deleteById(id);
                    } else {
                        content.state = Content.ContentState.CONTENT_STATE_DELETE.STATE;
                        content.update = new Date();
                        content.status = Content.ContentStatus.CONTENT_STATUS_LOCAL.STATUS;
                        content = cached.save(content);
                    }
                    return content;

                })
                .map(content -> {
                    logger.info("Delete Content: " + content);
                    submit(content);
                    syncDelete(content, context.auto);
                    return content;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<Content> subscribe() {
        return publisher;
    }

    public void upload(Content content) {
        syncCreate(content, true);
    }

    protected void submit(Content content) {
        sink.next(content);
    }

    protected Flux<Content> clipboard() {

        return Mono
                .fromCallable(() -> cached.getContentsByStateEqualsOrderByUpdateDesc(
                        Content.ContentState.CONTENT_STATE_NORMAL.STATE))
                .flatMapIterable((contents)->contents)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public void refresh() {
        if(context.auto) {
            gets(context.account)
                    .doOnError(e ->
                            logger.error("Failed to refresh content: " + e)
                    )
                    .map(content -> {
                        content.status = Content.ContentStatus.CONTENT_STATUS_CLOUD.STATUS;
                        return content;
                    })
                    .map(this::save)
                    .subscribe(this::submit);
        }
        clipboard().subscribe(sink::next);
    }

    private void syncDelete(Content content, boolean auto) {

        if(!auto) {
            return;
        }

        delete(content.id, context.account, content.update)
                .map(c -> {
                    c.uuid = content.uuid;
                    if(c.state == Content.ContentState.CONTENT_STATE_DELETE.STATE) {
                        cached.deleteById(c.uuid);
                    }
                    return c;
                })
                .doOnError(e -> {
                    logger.error("Failed to delete content: " + e);
                })
                .subscribe(sink::next);
    }

    private void syncCreate(Content content, boolean auto) {
        if(!auto) {
            return;
        }

        ContentModel model = new ContentModel();
        model.update = content.update;
        model.create = content.create;
        model.content = content.content;
        create(model, context.account)
                .map(c -> {
                    // save to local

                    c.uuid = content.uuid;
                    c.status = Content.ContentStatus.CONTENT_STATUS_CLOUD.STATUS;
                    if(c.state == Content.ContentState.CONTENT_STATE_DELETE.STATE) {
                        cached.deleteById(content.uuid);
                        return c;
                    } else {
                        return cached.save(c);
                    }
                })
                .doOnError(e -> {
                    logger.error("Failed to create content: " + e);
                })
                .subscribe(sink::next);
    }

    @Override
    public void onApplicationEvent(ClipboardEvent event) {
        switch (event.getType()) {
            case CLIPBOARD_SYNC_EVENT:
                ClipboardEventModel model = (ClipboardEventModel) event.getPayloud().get("event");
                Optional<Content> contentOptional = cached.findContentByIdEquals(model.id);

                if(model.type == ClipboardEventModel.ClipboardContentEventType.CONTENT_STATE_EVENT.EVENT) {
                    if(contentOptional.isPresent()) {
                        Content content = contentOptional.get();
                        cached.deleteById(content.uuid);
                        content.state = Content.ContentState.CONTENT_STATE_DELETE.STATE;
                        content.status = Content.ContentStatus.CONTENT_STATUS_CLOUD.STATUS;
                        sink.next(content);
                    }
                    break;
                }

                if(contentOptional.isEmpty()) {
                    // no id found
                    get(model.source, model.id)
                            .map(content -> {
                                content.status = Content.ContentStatus.CONTENT_STATUS_CLOUD.STATUS;
                                return content;
                            })
                            .map(this::save).subscribe(sink::next);
                    break;
                }
                Content local = contentOptional.get();
                if(local.update.equals(model.version)) {
                    // no need update
                    break;
                }

                // save to local
                get(model.source, model.id)
                        .map(content -> {
                            content.status = Content.ContentStatus.CONTENT_STATUS_CLOUD.STATUS;
                            return content;
                        })
                        .map(this::save)
                        .subscribe(sink::next);

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
        model.content = text;
        model.state = Content.ContentState.CONTENT_STATE_NORMAL.STATE;
        model.create = model.update = new Date();
        model.update = new Date();
        model.create = new Date();
        return model;
    }

    public Mono<Content> create(ContentModel model, String account) {
        return client
                .post()
                .uri("/clipboard/account/{account}/content", account)
                .body(Mono.just(model), ContentModel.class)
                .retrieve().bodyToMono(Content.class);
    }

    public Mono<Content> delete(String id, String account, Date time) {
        return client
                .patch()
                .uri("/clipboard/account/{account}/content/{content}", account, id)
                .body(Mono.just(time), Date.class)
                .retrieve().bodyToMono(Content.class);
    }

    public Flux<Content> gets(String account) {
        return client
                .get()
                .uri("/clipboard/account/{account}/contents", account)
                .retrieve().bodyToFlux(Content.class);
    }

    public Mono<Content> get(String account, String id) {
        return client
                .get()
                .uri("/clipboard/account/{account}/content/{content}", account, id)
                .retrieve().bodyToMono(Content.class);
    }

    private Content save(Content content) {
        Optional<Content> optionalContent =
                cached.findContentByHashEquals(content.hash);
        if(optionalContent.isEmpty()) {
            // no hash equal find
            content.uuid = UUID.randomUUID().toString();
            return cached.save(content);
        }

        Content local = optionalContent.get();

        if(local.content.equals(content.content)) {
            // identical found
            content.uuid = local.uuid;
            return cached.save(content);
        }

        // not found in local
        content.uuid = UUID.randomUUID().toString();
        return cached.save(content);
    }
}
