package com.example.clipboard.client.service.worker.event;

import com.example.clipboard.client.lifecycle.event.AppEvent;
import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.repository.model.ContentModel;

import java.util.Map;

public class ClipboardEvent extends AppEvent {

    private final EventType type;
    private final Map<String, Object> payload;

    public ClipboardEvent(EventSource source,
                          EventType type,
                          Map<String, Object> payload) {
        super(source);
        this.type = type;
        this.payload = payload;
    }

    public Map<String, Object> getPayloud() {
        return payload;
    }

    public EventType getType() {
        return type;
    }

    @Override
    public EventSource getSource() {
        return (EventSource)super.getSource();
    }

    public static enum EventSource {
        LOCAL_SOURCE,
        CLOUD_SOURCE
    }

    public static enum EventType {
        CLIPBOARD_REPORT,
        CLIPBOARD_UPDATE,
        CLIPBOARD_CREATE,
        CLIPBOARD_CHECK
    }

}
