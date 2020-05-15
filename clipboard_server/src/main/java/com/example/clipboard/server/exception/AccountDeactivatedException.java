package com.example.clipboard.server.exception;

import org.springframework.http.HttpStatus;

public class AccountDeactivatedException extends DomainException {
    public HttpStatus status = HttpStatus.BAD_REQUEST;
}
