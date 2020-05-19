package com.example.clipboard.client.event;

import org.springframework.context.ApplicationEvent;

public class AppStartEvent extends AppEvent {
    public AppStartEvent(Object source) {
        super(source);
    }
}
