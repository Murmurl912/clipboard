package com.clipboard.clipboard_store.endpoint;

import com.clipboard.clipboard_store.endpoint.model.ContentModel;
import com.clipboard.clipboard_store.endpoint.model.StateModel;
import com.clipboard.clipboard_store.event.ClipboardEvent;
import com.clipboard.clipboard_store.repository.entity.ClipboardContent;
import com.clipboard.clipboard_store.service.ClipboardEventPublisher;
import com.clipboard.clipboard_store.service.ClipboardService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.File;
import java.util.Date;

@RestController
public class ClipboardEndpoint {


    private final ClipboardEventPublisher publisher;
    private final ClipboardService service;

    public ClipboardEndpoint(ClipboardEventPublisher publisher,
                             ClipboardService service) {
        this.publisher = publisher;
        this.service = service;
    }

    @GetMapping(value = "/clipboard/account/{account}/content/versions",
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ClipboardContent> versions(@PathVariable String account) {
        return service.versions(account);
    }

    @GetMapping(value = "/clipboard/account/{account}/content/{content}/version",
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ClipboardContent> version(@PathVariable String account,
                                          @PathVariable String content) {
        return service.version(content);
    }

    @GetMapping(value = "/clipboard/account/{account}/contents",
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Flux<ClipboardContent> gets(@PathVariable String account) {
        return service.gets(account);
    }

    @GetMapping("/clipboard/account/{account}/content/{content}")
    public Mono<ClipboardContent> get(@PathVariable String account,
                                      @PathVariable String content) {
        return service.get(content);
    }

    @PostMapping("/clipboard/account/{account}/content")
    public Mono<ClipboardContent> content(@PathVariable String account,
                                        @RequestBody @Valid ContentModel contentModel) {
        ClipboardContent data = new ClipboardContent();
        data.content = contentModel.content;
        data.update = contentModel.update;
        data.create = contentModel.create;
        data.account = account;
        return service.create(account, data);
    }

    @PatchMapping("/clipboard/account/{account}/content/{content}")
    public Mono<ClipboardContent> delete(@PathVariable String account,
                                         @PathVariable String content,
                                         @RequestBody Date time) {
        return service.delete(account, content, time);
    }

    @GetMapping(value = "/clipboard/account/{account}/event",
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ClipboardEvent> listen(@PathVariable String account,
                                       WebSession webSession) {
        return publisher.subscribe()
                .filter(clipboardEvent -> clipboardEvent.getSource().equals(account))
                .map(event -> {
                    System.out.println(event);
                    return event;
                });
    }
}
