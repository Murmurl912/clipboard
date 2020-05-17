package com.example.clipboard.client.event;

import org.springframework.context.ApplicationEvent;

public class ClipboardUpdateEvent extends ApplicationEvent {

    public String content;

    public ClipboardUpdateEvent(Object source, String content) {
        super(source);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
