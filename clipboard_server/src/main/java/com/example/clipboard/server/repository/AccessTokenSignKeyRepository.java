package com.example.clipboard.server.repository;

import com.example.clipboard.server.entity.temp.AccessTokenSignKey;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccessTokenSignKeyRepository extends ReactiveMongoRepository<AccessTokenSignKey, String> {

    public Mono<AccessTokenSignKey> findFirstByStatusEquals(Integer status);
}
