package com.example.clipboard.client.service;

import com.example.clipboard.client.repository.entity.Content;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ClipboardStore {

    public Flux<Content> clipboard() {
        return null;
    }

    public Mono<Content> create() {
        // insert to local cache
        return null;
    }

    
}
