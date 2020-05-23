package com.example.clipboard.server.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends DomainException {

    public HttpStatus status = HttpStatus.NOT_FOUND;

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}
