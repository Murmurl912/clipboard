package com.example.clipboard.server.endpoint.account;


import com.example.clipboard.server.model.AuthenticationModel;
import com.example.clipboard.server.model.SignInModel;
import com.example.clipboard.server.model.SignUpModel;
import com.example.clipboard.server.model.UpdateModel;
import com.example.clipboard.server.service.reactive.AccountReactiveService;
import com.example.clipboard.server.service.reactive.AuthenticationReactiveService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AccountEndpoint {

    @Autowired
    private AccountReactiveService accountService;
    @Autowired
    private AuthenticationReactiveService authenticationService;

    /**
     * create a new account
     * @param model singup model
     * @return void
     */
    @PutMapping("/account")
    public Publisher<ResponseEntity<?>> signup(@RequestBody Mono<SignUpModel> model) {
        return accountService.register(model)
                .map(account -> ResponseEntity.ok().build());
    }

    /**
     * public
     * request activate an account
     * @param username account useranme
     * @return verification code id
     */
    @GetMapping("/account/{username}/activation")
    public Publisher<ResponseEntity<?>> activateRequest(@PathVariable String username) {
        return accountService.activateRequest(Mono.just(username))
                .map(code -> {
                    return ResponseEntity.ok().body(code);
                });
    }

    /**
     * public
     * activate an account
     * @param username username
     * @return void
     */
    @PostMapping("/account/{username}/activation")
    public Publisher<ResponseEntity<?>> activate(@PathVariable String username) {
        return accountService.activate(Mono.just(username))
                .map(aVoid -> ResponseEntity.ok().build());
    }

    /**
     * public
     * sign in
     * @param model sign in model, support username and password currently
     * @return access token
     */
    @PostMapping("/account")
    public Mono<ResponseEntity<Map<String, Object>>> signin(@RequestBody SignInModel model) {
        return authenticationService
                .authenticate(Mono.just(model)
                        .map(m ->
                                new AuthenticationModel() {
                            @Override
                            public AuthenticationType getType() {
                                return AuthenticationType.AUTHENTICATION_TYPE_PASSWORD;
                            }

                            @Override
                            public Map<String, Object> getData() {
                                Map<String, Object> map = new HashMap<>();
                                map.put(AuthenticationModel.username, m.username);
                                map.put(AuthenticationModel.password, m.password);
                                return map;
                            }
                        })
                )
                .flatMap(token -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return Mono.fromCallable(()-> Pair.of(token, objectMapper.writeValueAsString(token)));
                })
                .map(pair -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("token", pair.getFirst());
                    map.put("encoded", pair.getSecond());
                    return ResponseEntity.ok().body(map);
                });
    }

    /**
     * update username
     * @param account account id
     * @param username new username
     * @return void
     */
    @PatchMapping("/account/{account}/username")
    public Publisher<ResponseEntity<?>> updateUsername(@PathVariable String account,
                                                       @RequestBody String username) {
        UpdateModel model = new UpdateModel();
        model.username = username;
        model.account = account;

        return accountService
                .updateUsername(Mono.just(model))
                .map(aVoid -> ResponseEntity.ok().build());
    }

    @PatchMapping("/account/{account}/avatar")
    public Publisher<ResponseEntity<?>> updateAvatar(@PathVariable String account,
                                                     @RequestBody String avatar) {
        UpdateModel model = new UpdateModel();
        model.account = account;
        model.avatar = avatar;

        return accountService.updateAvatar(Mono.just(model)).map(aVoid -> ResponseEntity.ok().build());
    }

    /**
     * public
     * update password request
     * @param username username
     * @return verification code id
     */
    @GetMapping("/account/{username}/password")
    public Mono<ResponseEntity<?>> updatePassword(@PathVariable String username) {
        return accountService.requestUpdatePassword(Mono.just(username))
                .map(code -> ResponseEntity.ok().body(code));
    }

    /**
     * update password
     * @param username account username
     * @param model update model
     * @return void
     */
    @PostMapping("/account/{username}/password")
    public Publisher<ResponseEntity<?>> updatePassword(@PathVariable String username,
                                                       @RequestBody UpdateModel model) {
        return accountService.updatePassword(Mono.just(model))
                .map(aVoid -> ResponseEntity.ok().build());
    }

    /**
     *
     * @param account account name
     * @return verification code id
     */
    @GetMapping("/account/{account}/email")
    public Publisher<ResponseEntity<?>> requestUpdateEmail(@PathVariable String account) {
        UpdateModel model = new UpdateModel();
        model.account = account;
        return accountService.requestUpdateEmail(Mono.just(account))
                .map(code -> ResponseEntity.ok().body(code));
    }


    @PostMapping("/account/{account}/email")
    public Publisher<ResponseEntity<?>> updateEmail(@PathVariable String account,
                                                    @RequestBody UpdateModel model) {
        model.account = account;
        return accountService.updateEmail(Mono.just(model))
                .map(code -> ResponseEntity.ok().body(code));
    }



    /**
     * get account info
     * @param account account id
     * @return account without avatar
     */
    @GetMapping("/account/{account}")
    public Publisher<ResponseEntity<?>> account(@PathVariable String account) {
        return accountService.getWithoutAvatarById(Mono.just(account))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/account/{account}/avatar")
    public Publisher<ResponseEntity<?>> avatar(@PathVariable String account) {
        return accountService.getAvatarById(Mono.just(account))
                .map(ResponseEntity::ok);
    }


}
