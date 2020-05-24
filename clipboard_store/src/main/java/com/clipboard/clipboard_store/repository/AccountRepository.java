package com.clipboard.clipboard_store.repository;

import com.clipboard.clipboard_store.repository.entity.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
    Mono<Account> findAccountByUsernameEqualsAndPasswordEquals(String username, String password);

    Mono<Long> countAccountByUsernameEquals(String username);

    Mono<Long> countAccountByEmailEquals(String email);

}
