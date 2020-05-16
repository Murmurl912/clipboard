package com.example.clipboard.client.event;

import com.example.clipboard.client.entity.Content;
import org.springframework.context.ApplicationEvent;

public class ContentEvent extends ApplicationEvent {

    private ContentEventType eventType;
    private Content before;
    private Content now;

    public ContentEvent(Object source, Content before, Content now, ContentEventType type) {
        super(source);
        this.eventType = type;
        this.before = before;
        this.now = now;
    }

    public ContentEventType getEventType() {
        return eventType;
    }

    public Content getBefore() {
        return before;
    }

    public Content getNow() {
        return now;
    }

    public enum ContentEventType {
        CONTENT_EVENT_TYPE_CREATION,
        CONTENT_EVENT_TYPE_DELETE,
        CONTENT_EVENT_TYPE_UPDATE
    }
}
