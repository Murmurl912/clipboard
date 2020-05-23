package com.example.clipboard.server.model;

import org.springframework.http.HttpStatus;

public enum  ResponseErrorMessage {
    USERNAME_CONFLICT(HttpStatus.CONFLICT),
    EMAIL_ADDRESS_CONFLICT(HttpStatus.CONFLICT),
    REQUEST_BESSY(HttpStatus.BAD_REQUEST),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST);



    public HttpStatus status;
    ResponseErrorMessage(HttpStatus status) {
        this.status = status;
    }
}
