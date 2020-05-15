package com.example.clipboard.client.event;

import org.springframework.context.ApplicationEvent;

public class ClipboardEvent extends ApplicationEvent {

    public String content;

    public ClipboardEvent(Object source, String content) {
        super(source);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
