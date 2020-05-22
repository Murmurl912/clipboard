package com.clipboard.clipboard_store.endpoint;

import com.clipboard.clipboard_store.endpoint.model.LoginModel;
import com.clipboard.clipboard_store.endpoint.model.LoginResponseModel;
import com.clipboard.clipboard_store.endpoint.model.RegisterModel;
import com.clipboard.clipboard_store.repository.AccountRepository;
import com.clipboard.clipboard_store.repository.entity.Account;
import com.clipboard.clipboard_store.service.JwtTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;

@RestController
public class AccountEndpoint {
    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private AccountRepository repository;
    @Autowired
    private JwtTools tools;

    @PostMapping("/clipboard/account")
    public Mono<LoginResponseModel> login(@RequestBody @Valid LoginModel loginModel) {
        return repository.findAccountByUsernameEqualsAndPasswordEquals(loginModel.username, loginModel.password)
                .switchIfEmpty(Mono.error(new RuntimeException()))
                .map(account -> {
                    String token = tools.generateToken(account);
                    LoginResponseModel model = new LoginResponseModel();
                    model.token = token;
                    model.username = account.username;
                    model.email = account.email;
                    model.id = account.id;

                    return model;
                });
    }

    @PutMapping("/clipboard/account")
    public Mono<Account> register(@RequestBody @Valid RegisterModel registerModel) {
        return repository.countAccountByEmailEquals(registerModel.email)
                .zipWith(repository.countAccountByUsernameEquals(registerModel.username))
                .handle((tuple, sink) -> {
                    if (tuple.getT1() > 0) {
                        sink.error(new RuntimeException());
                        return;
                    }

                    if(tuple.getT2() > 0) {
                        sink.error(new RuntimeException());
                        return;
                    }

                    Account account = new Account();
                    account.create = new Date();
                    account.update = new Date();
                    account.email = registerModel.email;
                    account.password = registerModel.password;
                    account.username = registerModel.username;
                    sink.next(account);
                })
                .cast(Account.class)
                .flatMap(repository::save)
                .map(account -> {
                    account.password = null;
                    return account;
                });
    }

    @GetMapping("/clipboard/account/{account}")
    public Mono<Account> get(@PathVariable String account) {
        return repository.findById(account);
    }

}
