package com.example.clipboard.server.endpoint.clipboard;

import com.example.clipboard.server.entity.Content;
import com.example.clipboard.server.service.reactive.ClipboardReactiveService;
import org.reactivestreams.Publisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class ClipboardEndpoint {

    private final ClipboardReactiveService service;

    public ClipboardEndpoint(ClipboardReactiveService service) {
        this.service = service;
    }

    /**
     * add content to clipboard
     */
    @PostMapping("/clipboard/{owner}/content")
    public Mono<ResponseEntity<?>> add(@PathVariable String owner,
                                       @RequestBody Mono<Content> contentMono) {
        return service
                .create(contentMono.handle((content, sink) -> {
                    content.account = owner;
                    sink.next(content);
                }))
                .map(ResponseEntity::ok);
    }

    /**
     * delete content from clipboard
     * @param content content id
     */
    @DeleteMapping("/clipboard/content/{content}")
    public Mono<ResponseEntity<?>> delete(@PathVariable String content) {
        return service.delete(Mono.just(content)).map(ResponseEntity::ok);
    }

    /**
     * update content from clipboard
     * @param content content
     */
    @PatchMapping("/clipboard/content/{content}")
    public Publisher<ResponseEntity<?>> update(@PathVariable String content,
                                               @RequestBody Mono<Content> contentMono) {
        return service.update(contentMono).map(ResponseEntity::ok);
    }

    /**
     * get content from clipboard by id
     * @param content content id
     */
    @GetMapping("/clipboard/content/{content}")
    public Publisher<ResponseEntity<?>> get(@PathVariable String content) {
        return service.get(Mono.just(content)).map(ResponseEntity::ok);
    }

    @GetMapping("/clipboard/{owner}/content")
    public Publisher<ResponseEntity<?>> getByAccount(@PathVariable String owner) {
        return service.getByAccount(Mono.just(owner)).map(ResponseEntity::ok);
    }

}
