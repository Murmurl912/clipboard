package com.example.clipboard.client.lifecycle.event;

import org.springframework.context.ApplicationEvent;

public class AppEvent extends ApplicationEvent {
    public AppEvent(Object source) {
        super(source);
    }
}
