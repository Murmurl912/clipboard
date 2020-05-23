package com.example.clipboard.server.exception;

import org.springframework.http.HttpStatus;

public class ContentNotFoundException extends DomainException {
    public HttpStatus status = HttpStatus.NOT_FOUND;

    public ContentNotFoundException() {
        super();
    }

    public ContentNotFoundException(String message) {
        super(message);
    }

    public ContentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContentNotFoundException(Throwable cause) {
        super(cause);
    }
}
