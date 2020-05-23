package com.example.clipboard.server.exception;

import org.springframework.http.HttpStatus;

public class DomainException extends Exception {

    public HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;

    public DomainException() {
        super();
    }

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainException(Throwable cause) {
        super(cause);
    }

}
