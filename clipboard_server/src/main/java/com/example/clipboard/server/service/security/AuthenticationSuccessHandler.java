package com.example.clipboard.server.service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

public class AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange,
                                              Authentication authentication) {
        return Mono.empty();
    }
}
