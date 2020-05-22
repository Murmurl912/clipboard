package com.example.clipboard.client.service;

import com.example.clipboard.client.repository.CachedContentRepository;
import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.repository.model.ContentModel;
import com.example.clipboard.client.service.worker.event.ClipboardEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ReactiveClipboardService implements ApplicationListener<ClipboardEvent> {

    private final CachedContentRepository cached;
    private final MessageDigest digest;
    private final FluxProcessor<Content, Content> processor;
    private final FluxSink<Content> sink;
    private final Flux<Content> publisher;
    private final int historyCount = 100;
    private ApplicationContext context;
    private BlockingQueue<Content> contents;
    private final WebClient client;
    private TaskExecutor executor;
    public ReactiveClipboardService(CachedContentRepository cached,
                                    MessageDigest digest,
                                    ApplicationContext context,
                                    WebClient.Builder builder,
                                    @Qualifier("sync") TaskExecutor executor, TaskScheduler scheduler) {
        EmitterProcessor<Content> clipboardProcessor = EmitterProcessor.create();
        this.cached = cached;
        this.digest = digest;
        this.context = context;
        this.contents = new LinkedBlockingQueue<>();
        client = builder.baseUrl("http://localhost:8080").build();
        processor = ReplayProcessor.create(historyCount);
        sink = processor.sink();
        publisher = processor.share();
        this.executor = executor;
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
                            content.state = Content.ContentState.CONTENT_STATE_NORMAL.STATE;
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
                    contents.offer(content);
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
                    content.state = Content.ContentState.CONTENT_STATE_DELETE.STATE;
                    content.update = new Date();
                    return content;
                })
                .map(cached::save)
                .map(content -> {
                    submit(content);
                    contents.offer(content);
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

        return Mono
                .fromCallable(() -> cached.getContentsByStateEqualsOrderByUpdateDesc(
                        Content.ContentState.CONTENT_STATE_NORMAL.STATE))
                .map(list -> {
                    System.out.println(list);
                    return list;
                })
                .flatMapIterable((contents)->contents)
                .map(c -> {
                    return c;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public void refresh() {
        gets("test").subscribe(sink::next);
        clipboard().subscribe(sink::next);
    }

    @Override
    public void onApplicationEvent(ClipboardEvent event) {
        switch (event.getType()) {
            case CLIPBOARD_CREATE:
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

    public Mono<Content> delete(String id, String account) {
        return client
                .delete()
                .uri("/clipboard/account/{account}/content/{content}", account, id)
                .retrieve().bodyToMono(Content.class);
    }

    public Flux<Content> gets(String account) {
        return client
                .get()
                .uri("/clipboard/account/{account}/contents", account)
                .retrieve().bodyToFlux(Content.class);
    }

}
