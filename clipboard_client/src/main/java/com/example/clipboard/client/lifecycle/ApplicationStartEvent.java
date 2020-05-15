package com.example.clipboard.client.lifecycle;

import org.springframework.context.ApplicationEvent;

public class ApplicationStartEvent extends ApplicationEvent {
    public ApplicationStartEvent(Object source) {
        super(source);
    }
}
