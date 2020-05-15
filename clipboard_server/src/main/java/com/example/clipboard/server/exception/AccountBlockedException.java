package com.example.clipboard.server.exception;

import org.springframework.http.HttpStatus;

public class AccountBlockedException extends DomainException {
    public HttpStatus httpStatus = HttpStatus.FORBIDDEN;

    public AccountBlockedException() {
        super();
    }

    public AccountBlockedException(String message) {
        super(message);
    }

    public AccountBlockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountBlockedException(Throwable cause) {
        super(cause);
    }
}
