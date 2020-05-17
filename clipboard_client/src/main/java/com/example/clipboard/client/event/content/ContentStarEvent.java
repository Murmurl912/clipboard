package com.example.clipboard.client.event.content;

import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.ContentEvent;

/**
 * an event signify content's star flag changed
 */
public class ContentStarEvent extends ContentEvent {

    public ContentStarEvent(Object source, Content before, Content now) {
        super(source, before, now);
    }

}
