package com.clipboard.clipboard_store.event;

import org.springframework.context.ApplicationEvent;

public abstract class ClipboardEvent extends ApplicationEvent {

    public ClipboardEvent(String source) {
        super(source);
    }

}
