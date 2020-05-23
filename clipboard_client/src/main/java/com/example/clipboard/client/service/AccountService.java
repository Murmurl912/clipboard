package com.example.clipboard.client.service;

import com.example.clipboard.client.repository.model.LoginModel;
import com.example.clipboard.client.repository.model.LoginResponseModel;
import com.example.clipboard.client.repository.model.RegisterModel;
import com.example.clipboard.client.repository.model.RegisterResponse;
import com.example.clipboard.client.service.exception.EmailRegisteredException;
import com.example.clipboard.client.service.exception.LoginFailedException;
import com.example.clipboard.client.service.exception.UserNameRegisteredException;
import com.example.clipboard.client.service.worker.event.AccountLoginEvent;
import com.example.clipboard.client.service.worker.event.AccountLogoutEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AccountService {

    private final WebClient client;
    private final AppContext context;
    private final ApplicationEventPublisher publisher;

    public AccountService(WebClient.Builder builder,
                          AppContext context,
                          ApplicationEventPublisher publisher) {
        this.context = context;
        client = builder.baseUrl(context.baseUrl).build();
        this.publisher = publisher;
    }

    public Mono<LoginResponseModel> signIn(String username,
                                           String password) {
        LoginModel model = new LoginModel();
        model.username = username;
        model.password = password;
        return client.post()
                .uri("/clipboard/account")
                .body(Mono.just(model), LoginModel.class)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.UNAUTHORIZED),
                        response -> {
                            return Mono.error(new LoginFailedException());
                        })
                .bodyToMono(LoginResponseModel.class)
                .map(login -> {
                    context.account = login.id;
                    context.token = login.token;
                    context.username = login.username;
                    context.email = login.email;
                    publisher.publishEvent(new AccountLoginEvent(login));
                    return login;
                });
    }

    public void signOut() {
        publisher.publishEvent(new AccountLogoutEvent(this));
    }

    public Mono<RegisterResponse> register(String username, String password, String email) {
        RegisterModel model = new RegisterModel();
        model.username = username;
        model.password = password;
        model.email = email;
        return client.put()
                .uri("/clipboard/account")
                .body(Mono.just(model), LoginModel.class)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.CONFLICT),
                        response -> {
                            return Mono.error(new EmailRegisteredException());
                        })
                .onStatus(status -> status.equals(HttpStatus.FORBIDDEN), response -> {
                    return Mono.error(new UserNameRegisteredException());
                })
                .bodyToMono(RegisterResponse.class);
    }


}
