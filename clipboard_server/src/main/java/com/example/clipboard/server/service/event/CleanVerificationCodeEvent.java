package com.example.clipboard.server.service.event;

import org.springframework.context.ApplicationEvent;

public class CleanVerificationCodeEvent extends ApplicationEvent {
    public CleanVerificationCodeEvent(Object source) {
        super(source);
    }
}
