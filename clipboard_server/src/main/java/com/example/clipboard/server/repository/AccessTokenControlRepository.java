package com.example.clipboard.server.repository;

import com.example.clipboard.server.entity.temp.AccessTokenControl;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface AccessTokenControlRepository extends ReactiveMongoRepository<AccessTokenControl, String> {
    public Mono<Long> countAccessTokenControlByTokenEquals(String token);

    public Mono<AccessTokenControl> findFirstByTokenEquals(String token);
}
