package com.example.clipboard.client.service;

import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.repository.model.ContentCreateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Lazy(false)
@Service
public class CloudClipboardService implements Consumer<Content> {

    private final ReactiveClipboardService service;
    private final AppContext appContext;
    private final WebClient client;
    private BlockingQueue<Content> local;

    public CloudClipboardService(ReactiveClipboardService service,
                                 AppContext context,
                                 WebClient.Builder builder) {
        this.service = service;
        this.appContext = context;
        local = new LinkedBlockingQueue<>();
        client = builder.baseUrl(appContext.baseUrl).build();
        service.subscribe().subscribe(this);
    }

    @Override
    public void accept(Content content) {
        local.offer(content);
    }

    public void sync() throws InterruptedException {
        Content content = local.peek();
        if(content == null) {
            return;
        }
        if(content.status == Content.ContentStatus.CONTENT_STATUS_LOCAL.STATUS) {

        } else {

        }
        ContentCreateModel model = new ContentCreateModel();
    }

    public Mono<Content> version(String content, String account, String token) {
        return client
                .get()
                .uri("/clipboard/account/{account}/content/{content}/version",
                        account)
                .retrieve().bodyToMono(Content.class);
    }

    public Mono<Content> create(ContentCreateModel model) {
        return client
                .post()
                .uri("/clipboard/account/{account}/content", model.account)
                .body(Mono.just(model), ContentCreateModel.class)
                .retrieve().bodyToMono(Content.class);
    }

}
