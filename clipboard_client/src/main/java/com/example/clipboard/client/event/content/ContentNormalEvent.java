package com.example.clipboard.client.event.content;

import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.ContentEvent;

public class ContentNormalEvent extends ContentEvent {
    public ContentNormalEvent(Object source, Content before, Content now) {
        super(source, before, now);
    }
}
