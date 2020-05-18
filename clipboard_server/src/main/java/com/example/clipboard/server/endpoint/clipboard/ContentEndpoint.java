package com.example.clipboard.server.endpoint.clipboard;

import com.example.clipboard.server.entity.Content;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

@RestController
public class ContentEndpoint {

    public Mono<Content> star(String id,
                              Boolean star,
                              Timestamp timestamp) {
        return null;
    }

    public Mono<Content> state(String id,
                      Integer state,
                      Timestamp timestamp) {
        return null;
    }

    public Mono<Content> content(String id,
                        String content,
                        Timestamp timestamp) {
        return null;
    }

    public Mono<Content> create(String account,
                                String content,
                                String device,
                                Boolean star,
                                Integer state,
                                Timestamp timestamp) {
        return null;
    }

    public Mono<Content> update(Content content) {
        return null;
    }


    public Mono<Content> delete(String id) {
        return null;
    }

    public Mono<Content> get(String id) {
        return null;
    }


    public Flux<Content> gets(String account) {
        return null;
    }

    public Flux<Content> check(String account) {
        return null;
    }

}
