package com.example.clipboard.client.event.content;

import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.ContentEvent;

/**
 * an event represent a new content is added to local cache
 * this can be triggered by insert cloud content to local cache
 * or a from clipboard change etc.
 */
public class ContentCreateEvent extends ContentEvent {

    public ContentCreateEvent(Object source,
                              Content before,
                              Content now) {
        super(source, before, now);
    }
}
