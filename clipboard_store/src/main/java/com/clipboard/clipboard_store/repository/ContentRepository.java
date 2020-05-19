package com.clipboard.clipboard_store.repository;

import com.clipboard.clipboard_store.repository.entity.ClipboardContent;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ContentRepository extends ReactiveMongoRepository<ClipboardContent, String> {

    public Mono<ClipboardContent> findFirstByHashEqualsAndAccountEquals(byte[] has, String account);


    @Query(fields = "{content: 0}")
    public Flux<ClipboardContent> findFirstByIdEquals(String id);

    @Query(fields = "{content: 0}")
    public Flux<ClipboardContent> findAllByAccountEquals(String account);

    @Query(fields = "{content: 0}")
    public Flux<ClipboardContent> findAllByAccountEqualsAndStar(String account, Boolean star);

    @Query(fields = "{content: 0}")
    public Flux<ClipboardContent> findAllByAccountEqualsAndStateEquals(String account, Integer state);

    @Query(fields = "{content: 0}")
    public Flux<ClipboardContent> findAllByAccountEqualsAndStateEqualsAndStarEquals(String account, Integer state, Boolean star);


}
