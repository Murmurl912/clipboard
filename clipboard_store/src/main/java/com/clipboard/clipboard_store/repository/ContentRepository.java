package com.clipboard.clipboard_store.repository;

import com.clipboard.clipboard_store.repository.entity.ClipboardContent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ContentRepository extends ReactiveMongoRepository<ClipboardContent, String> {
    public Mono<Long> countClipboardContentByHashEquals(byte[] hash);

    public Mono<Long> countClipboardContentByHashEquals(Mono<byte[]> hash);

    public Mono<ClipboardContent> findFirstByHashEquals(byte[] hash);

    public Mono<ClipboardContent> findFirstByHashEquals(Mono<byte[]> hash);
}
