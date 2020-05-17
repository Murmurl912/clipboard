package com.example.clipboard.client.event;

import org.springframework.context.ApplicationEvent;

public class ClipboardEvent extends ApplicationEvent {
    public ClipboardEvent(Object source) {
        super(source);
    }
}
