package com.example.clipboard.client.event;

import org.springframework.context.ApplicationEvent;

public class ClipboardEvent extends AppEvent {
    public ClipboardEvent(Object source) {
        super(source);
    }
}
