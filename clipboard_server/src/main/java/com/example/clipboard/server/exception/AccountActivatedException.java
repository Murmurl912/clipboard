package com.example.clipboard.server.exception;

import org.springframework.http.HttpStatus;

public class AccountActivatedException extends DomainException {
    public HttpStatus status = HttpStatus.CONFLICT;

    public AccountActivatedException() {
        super();
    }

    public AccountActivatedException(String message) {
        super(message);
    }

    public AccountActivatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountActivatedException(Throwable cause) {
        super(cause);
    }
}
