package com.example.clipboard.server.exception;

import org.springframework.http.HttpStatus;

public class EmailRegisteredException extends DomainException {
    public HttpStatus status = HttpStatus.CONFLICT;

    public EmailRegisteredException() {
        super();
    }

    public EmailRegisteredException(String message) {
        super(message);
    }

    public EmailRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailRegisteredException(Throwable cause) {
        super(cause);
    }
}
