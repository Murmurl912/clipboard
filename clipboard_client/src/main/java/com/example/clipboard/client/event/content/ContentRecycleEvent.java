package com.example.clipboard.client.event.content;

import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.ContentEvent;

public class ContentRecycleEvent extends ContentEvent {
    public ContentRecycleEvent(Object source, Content before, Content now) {
        super(source, before, now);
    }
}
