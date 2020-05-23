package com.example.clipboard.server.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends DomainException {
    public HttpStatus status = HttpStatus.UNAUTHORIZED;

    public AuthenticationException() {
        super();
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }

}
