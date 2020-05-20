package com.example.clipboard.client.repository;

import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.repository.model.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Lazy
@Repository
public class RemoteContentRepository {

    private final WebClient client;
    private final String baseUrl = "http://localhost:8080";
    private final String account = "test";

    public RemoteContentRepository(WebClient.Builder builder) {
        client = builder.baseUrl(baseUrl).build();
    }

    public Mono<Content> version(String content) {
        return client
                .get().uri("/clipboard/account/{account}/content/{content}/version",
                        account)
                .retrieve().bodyToMono(Content.class);
    }

    public Flux<Content> versions() {
        return client
                .get()
                .uri("/clipboard/account/{account}/content/versions", account)
                .retrieve().bodyToFlux(Content.class);
    }

    public Flux<Content> contents(Boolean star) {
        return client
                .get()
                .uri("/clipboard/account/{account}/contents?star={star}",
                        account, star)
                .retrieve().bodyToFlux(Content.class);
    }

    public Flux<Content> contents() {
        return client
                .get()
                .uri("/clipboard/account/{account}/contents",
                        account)
                .retrieve().bodyToFlux(Content.class);
    }

    public Mono<Content> content(String id) {
        return client
                .get()
                .uri("/clipboard/account/{account}/content/{content}",
                        account, id)
                .retrieve().bodyToMono(Content.class);
    }

    public Mono<Content> create(ContentCreateModel model) {
        return client
                .post()
                .uri("/clipboard/account/{account}/content",
                        account)
                .body(Mono.just(model), ContentCreateModel.class)
                .retrieve().bodyToMono(Content.class);
    }

    public Mono<Content> star(String id,
                              ContentStarModel model) {
        return client
                .patch()
                .uri("/clipboard/account/{account}/content/{content}/star",
                        account, id)
                .body(Mono.just(model), ContentStarModel.class)
                .retrieve().bodyToMono(Content.class);
    }

    public Mono<Content> text(String id,
                              ContentTextModel model) {
        return client
                .patch()
                .uri("/clipboard/account/{account}/content/{content}/text",
                        account, id)
                .body(Mono.just(model), ContentTextModel.class)
                .retrieve().bodyToMono(Content.class);
    }

    public Mono<Content> state(String id,
                               ContentStateModel model) {
        return client
                .patch()
                .uri("/clipboard/account/{account}/content/{content}/state",
                        account, id)
                .body(Mono.just(model), ContentStateModel.class)
                .retrieve().bodyToMono(Content.class);
    }

    public Flux<ClipboardContentEvent> event() {
        return client
                .get()
                .uri("/clipboard/account/{account}/event",
                        account)
                .retrieve()
                .bodyToFlux(ClipboardContentEvent.class);
    }

}
