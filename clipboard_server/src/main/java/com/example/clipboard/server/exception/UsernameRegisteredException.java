package com.example.clipboard.server.exception;

import org.springframework.http.HttpStatus;

public class UsernameRegisteredException extends DomainException {

    public HttpStatus status = HttpStatus.CONFLICT;

    public UsernameRegisteredException() {
        super();
    }

    public UsernameRegisteredException(String message) {
        super(message);
    }

    public UsernameRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameRegisteredException(Throwable cause) {
        super(cause);
    }
}
