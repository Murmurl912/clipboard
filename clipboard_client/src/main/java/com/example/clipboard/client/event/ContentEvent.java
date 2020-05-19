package com.example.clipboard.client.event;

import com.example.clipboard.client.entity.Content;
import org.springframework.context.ApplicationEvent;

public class ContentEvent extends AppEvent {

    private final Content before;
    private final Content now;

    public ContentEvent(Object source,
                        Content before,
                        Content now) {
        super(source);
        this.before = before;
        this.now = now;
    }

    public Content getBefore() {
        return before;
    }

    public Content getNow() {
        return now;
    }

}
