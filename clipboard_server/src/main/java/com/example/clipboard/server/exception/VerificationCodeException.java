package com.example.clipboard.server.exception;

import org.springframework.http.HttpStatus;

public class VerificationCodeException extends DomainException {

    public HttpStatus status = HttpStatus.BAD_REQUEST;

    public VerificationCodeException() {
        super();
    }

    public VerificationCodeException(String message) {
        super(message);
    }

    public VerificationCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerificationCodeException(Throwable cause) {
        super(cause);
    }
}
