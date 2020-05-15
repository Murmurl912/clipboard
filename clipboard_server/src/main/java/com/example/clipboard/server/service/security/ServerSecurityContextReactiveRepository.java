package com.example.clipboard.server.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ServerSecurityContextReactiveRepository implements ServerSecurityContextRepository {

    @Autowired
    private AuthenticationReactiveManager manager;

    private final static String TOKEN_PREFIX = "Bearer";
    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(StringUtils.isEmpty(authHeader) || !authHeader.startsWith(TOKEN_PREFIX)) {
            return Mono.empty();
        } else {
            authHeader = authHeader.replace(TOKEN_PREFIX, "").trim();
            Authentication authentication = new UsernamePasswordAuthenticationToken(authHeader, authHeader);
            return manager.authenticate(authentication).map(SecurityContextImpl::new);
        }
    }

}
