package com.example.clipboard.client.event.clipboard;

import com.example.clipboard.client.event.ClipboardEvent;

/**
 * an event indicate system's clipboard is updated
 */
public class ClipboardUpdateEvent extends ClipboardEvent {
    private final String content;
    public ClipboardUpdateEvent(Object source, String content) {
        super(source);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
