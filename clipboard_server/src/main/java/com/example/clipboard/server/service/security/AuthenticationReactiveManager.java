package com.example.clipboard.server.service.security;

import com.example.clipboard.server.entity.temp.AccessToken;
import com.example.clipboard.server.service.reactive.AuthenticationReactiveService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;

@Component
public class AuthenticationReactiveManager implements ReactiveAuthenticationManager {

    private final AuthenticationReactiveService service;

    public AuthenticationReactiveManager(AuthenticationReactiveService service) {
        this.service = service;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        Mono<AccessToken> accessTokenMono = Mono.fromCallable(()-> new ObjectMapper()
                .readValue(
                        new String(Base64Utils.decodeFromString(
                                authentication.getCredentials().toString())),
                        AccessToken.class));

        return service.verification(accessTokenMono)
                .flatMap(bool -> {
                    if(bool) {
                        return accessTokenMono.map(token -> {
                            Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("user"));
                            UsernamePasswordAuthenticationToken
                                    authenticationToken = new UsernamePasswordAuthenticationToken(token.account, token.account, authorities);
                            SecurityContextHolder.getContext().
                                    setAuthentication(authenticationToken);
                            return authenticationToken;
                        });
                    } else {
                        return Mono.empty();
                    }
                });
    }
}
