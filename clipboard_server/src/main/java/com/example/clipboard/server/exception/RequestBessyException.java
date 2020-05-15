package com.example.clipboard.server.exception;

import org.springframework.http.HttpStatus;

public class RequestBessyException extends DomainException {
    public HttpStatus status = HttpStatus.CONFLICT;

    public RequestBessyException() {
        super();
    }

    public RequestBessyException(String message) {
        super(message);
    }

    public RequestBessyException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestBessyException(Throwable cause) {
        super(cause);
    }
}
