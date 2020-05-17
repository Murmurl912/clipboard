package com.example.clipboard.client.event;

import com.example.clipboard.client.entity.Content;

public class ContentTextChangeEvent extends ContentEvent {

    public ContentTextChangeEvent(Object source, Content before, Content now) {
        super(source, before, now);
    }
}
