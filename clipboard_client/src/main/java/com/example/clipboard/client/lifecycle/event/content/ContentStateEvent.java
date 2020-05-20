package com.example.clipboard.client.lifecycle.event.content;

import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.lifecycle.event.ContentEvent;

public class ContentStateEvent extends ContentEvent {

    public ContentStateEvent(String id,
                             Content content) {
        super(id, content);
    }

}
