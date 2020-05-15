package com.example.clipboard.server.service.reactive;

import com.example.clipboard.server.entity.Account;
import com.example.clipboard.server.exception.UserNotFoundException;
import com.example.clipboard.server.repository.AccountRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Service
public class UserDetailsReactiveService implements ReactiveUserDetailsService {

    private final AccountRepository repository;

    public UserDetailsReactiveService(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String s) {
        return repository.findAccountByUsernameEquals(s)
                .switchIfEmpty(Mono.error(UserNotFoundException::new))
                .map(account -> new UserDetails() {
                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return null;
                    }

                    @Override
                    public String getPassword() {
                        return account.password;
                    }

                    @Override
                    public String getUsername() {
                        return account.username;
                    }

                    @Override
                    public boolean isAccountNonExpired() {
                        return account.status.equals(Account.AccountStatus.ACCOUNT_STATUS_BLOCK.STATUS);
                    }

                    @Override
                    public boolean isAccountNonLocked() {
                        return account.status.equals(Account.AccountStatus.ACCOUNT_STATUS_REGISTERED.STATUS);
                    }

                    @Override
                    public boolean isCredentialsNonExpired() {
                        return true;
                    }

                    @Override
                    public boolean isEnabled() {
                        return true;
                    }
                });
    }
}
