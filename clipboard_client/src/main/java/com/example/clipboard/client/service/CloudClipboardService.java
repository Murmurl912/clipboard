package com.example.clipboard.client.service;

import com.example.clipboard.client.repository.CachedContentRepository;
import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.repository.model.ContentModel;
import com.example.clipboard.client.service.worker.ClipboardSyncEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Lazy(false)
@Service
public class CloudClipboardService implements ApplicationListener<ClipboardSyncEvent> {

    private final ReactiveClipboardService service;
    private final AppContext appContext;
    private final WebClient client;
    private BlockingQueue<Content> local;
    private CachedContentRepository repository;

    public CloudClipboardService(ReactiveClipboardService service,
                                 AppContext context,
                                 WebClient.Builder builder,
                                 CachedContentRepository repository) {
        this.service = service;
        this.appContext = context;
        this.repository = repository;
        local = new LinkedBlockingQueue<>();
        client = builder.baseUrl("http://localhost:8080").build();
    }

    @Override
    public void onApplicationEvent(ClipboardSyncEvent event) {
        Content content = event.getContent();
        sync(content, "test");
    }

    private void sync(Content content, String account) {
        content.account = account;
        if(content.status == Content.ContentStatus.CONTENT_STATUS_LOCAL.STATUS) {
            localSync(content);
        } else {
            cloudSync(content);
        }
    }

    private void localSync(Content content) {
        if(content.state == Content.ContentState.CONTENT_STATE_DELETE.STATE) {
            repository.deleteById(content.id);
            return;
        }

        ContentModel model = new ContentModel();
        model.create = content.create;
        model.update = content.update;
        model.content = content.content;
        create(model, content.account)
                .subscribe(c -> {
                    c.status = Content.ContentStatus.CONTENT_STATUS_CLOUD.STATUS;
                    c.uuid = content.id;
                    repository.save(c);
                });
    }

    private void cloudSync(Content content) {
        if(content.state == Content.ContentState.CONTENT_STATE_DELETE.STATE) {
            delete(content.id, content.account, content.update)
                    .subscribe();
            return;
        }

        ContentModel model = new ContentModel();
        model.create = content.create;
        model.update = content.update;
        model.content = content.content;
        create(model, content.account)
                .subscribe(c -> {
                    c.status = Content.ContentStatus.CONTENT_STATUS_CLOUD.STATUS;
                    c.uuid = content.id;
                    repository.save(c);
                });
    }

    public Mono<Content> version(String content, String account, String token) {
        return client
                .get()
                .uri("/clipboard/account/{account}/content/{content}/version",
                        account)
                .retrieve().bodyToMono(Content.class);
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
                .post()
                .uri("/clipboard/account/{account}/content/{content}/state", account, id)
                .body(Mono.just(time), Date.class)
                .retrieve().bodyToMono(Content.class);
    }

}
