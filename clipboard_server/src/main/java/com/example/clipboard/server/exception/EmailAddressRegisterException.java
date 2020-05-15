package com.example.clipboard.server.exception;

import org.springframework.http.HttpStatus;

public class EmailAddressRegisterException  extends DomainException {
    public HttpStatus status = HttpStatus.CONFLICT;

    public EmailAddressRegisterException() {
        super();
    }

    public EmailAddressRegisterException(String message) {
        super(message);
    }

    public EmailAddressRegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailAddressRegisterException(Throwable cause) {
        super(cause);
    }
}
