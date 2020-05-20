package com.example.clipboard.client.lifecycle.event.content;

import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.lifecycle.event.ContentEvent;

public class ContentCreateEvent extends ContentEvent {
    public ContentCreateEvent(String id,
                              Content content) {
        super(id, content);
    }
}
