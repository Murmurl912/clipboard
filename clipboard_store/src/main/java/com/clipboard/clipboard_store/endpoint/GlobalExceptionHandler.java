package com.clipboard.clipboard_store.endpoint;

import com.clipboard.clipboard_store.endpoint.exception.EmailConflictException;
import com.clipboard.clipboard_store.endpoint.exception.UsernameConflictException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {


    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        if(throwable instanceof EmailConflictException) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.CONFLICT);
        } else if(throwable instanceof UsernameConflictException) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        } else {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        }
        return Mono.empty();
    }
}
