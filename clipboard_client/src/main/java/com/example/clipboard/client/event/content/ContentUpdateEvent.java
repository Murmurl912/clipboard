package com.example.clipboard.client.event.content;

import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.ContentEvent;

/**
 * an event imply content's text or timestamp has changed
 */
public class ContentUpdateEvent extends ContentEvent {

    public ContentUpdateEvent(Object source,
                              Content before,
                              Content now) {
        super(source, before, now);
    }
}
