package com.example.clipboard.client.service;

import com.example.clipboard.client.repository.entity.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Service
public class CloudClipboardService implements Consumer<Content> {

    private final ReactiveClipboardService service;
    private final AppContext appContext;
    private final WebClient client;
    private BlockingQueue<Content> local;

    public CloudClipboardService(ReactiveClipboardService service,
                                 AppContext context, WebClient.Builder builder) {
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
        Content content = local.take();
        content.account = appContext.account;

    }


}
