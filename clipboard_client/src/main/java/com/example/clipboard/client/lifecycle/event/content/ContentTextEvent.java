package com.example.clipboard.client.lifecycle.event.content;

import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.lifecycle.event.ContentEvent;

public class ContentTextEvent extends ContentEvent {

    public ContentTextEvent(String id, Content content) {
        super(id, content);
    }

}
