package com.example.clipboard.server.repository;

import com.example.clipboard.server.entity.Account;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, String> {

    public Mono<Long> countAccountByUsernameEquals(String username);

    public Mono<Long> countAccountByEmailEquals(String email);

    public Mono<Long> countAccountByIdEqualsAndUsernameEquals(String id, String username);

    public Mono<Long> countAccountByIdEqualsAndEmailEquals(String id, String email);

    public Mono<Long> countAccountByOldEmailEquals(String email);

    public Mono<Long> countAccountById(String id);

    public Mono<Long> countAccountByUsernameEqualsAndPasswordEquals(String username, String password);

    public Mono<Account> findAccountByUsernameEquals(String username);

    @Query(fields="{password: 0, avatar: 0}")
    public Mono<Account> findAccountStatusByUsernameEquals(String username);

    @Query(fields="{password: 0, avatar: 0}")
    public Mono<Account> findAccountStatusByIdEquals(String id);


}
