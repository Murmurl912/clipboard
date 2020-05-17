package com.example.clipboard.client.event.content;

import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.ContentEvent;

/**
 * an event triggered when content is delete from local cache
 * and cloud database
 */
public class ContentDeleteEvent extends ContentEvent {
    public ContentDeleteEvent(Object source,
                              Content before,
                              Content now) {
        super(source, before, now);
    }
}
