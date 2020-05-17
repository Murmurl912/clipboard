package com.example.clipboard.client.event;

import com.example.clipboard.client.entity.Content;

public class ContentFlagChangeEvent extends ContentEvent {
    public ContentFlagChangeEvent(Object source,
                                  Content before,
                                  Content now) {
        super(source, before, now);
    }


}
