package com.example.clipboard.client.event.content;

import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.ContentEvent;

/**
 * an event indicate content archive flag has been changed
 */
public class ContentArchiveEvent extends ContentEvent {

    public ContentArchiveEvent(Object source, Content before, Content now) {
        super(source, before, now);
    }

}
