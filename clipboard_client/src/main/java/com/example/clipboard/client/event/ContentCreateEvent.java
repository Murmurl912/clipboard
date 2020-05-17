package com.example.clipboard.client.event;

import com.example.clipboard.client.entity.Content;

public class ContentCreateEvent extends ContentEvent {

    public ContentCreateEvent(Object source,
                              Content before,
                              Content now) {
        super(source, before, now);
    }
}
