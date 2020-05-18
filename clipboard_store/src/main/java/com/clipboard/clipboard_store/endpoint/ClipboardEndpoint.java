package com.clipboard.clipboard_store.endpoint;

import com.clipboard.clipboard_store.repository.entity.ClipboardContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ClipboardEndpoint {

    private final ApplicationEventPublisher publisher;
    public ClipboardEndpoint(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @GetMapping(value = "/clipboard/account/{account}/contents",
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Flux<ClipboardContent> gets(@PathVariable String account) {
        return null;
    }

    public Mono<ClipboardContent> get(String id) {
        return null;
    }

    public Mono<ClipboardContent> create(ClipboardContent content) {
        return null;
    }

    public Mono<ClipboardContent> star(String id) {
        return null;
    }

    public Mono<ClipboardContent> unstar(String id) {
        return null;
    }

    public Mono<ClipboardContent> archive(String id) {
        return null;
    }

    public Mono<ClipboardContent> unarchive(String id) {
        return null;
    }

    public Mono<ClipboardContent> recycle(String id) {
        return null;
    }

    public Mono<ClipboardContent> recover(String id) {
        return null;
    }

    public Mono<ClipboardContent> delete(String id) {
        return null;
    }
}
