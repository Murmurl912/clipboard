package com.example.clipboard.server.repository;

import com.example.clipboard.server.entity.Content;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ContentRepository extends ReactiveMongoRepository<Content, String> {
    @Query(fields = "{id: 1}")
    public Flux<Content> findAllByAccountEquals(Mono<String> account);
}
