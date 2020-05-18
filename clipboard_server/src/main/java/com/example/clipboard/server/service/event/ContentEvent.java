package com.example.clipboard.server.service.event;

import org.springframework.context.ApplicationEvent;

import java.sql.Timestamp;

public class ContentEvent extends ApplicationEvent {

    private final String account;
    private final Timestamp time;
    private final Integer event;
    public final String content;

    public ContentEvent(Object source,
                        String account,
                        Integer event,
                        Timestamp time,
                        String content) {
        super(source);
        this.account = account;
        this.time = time;
        this.event = event;
        this.content = content;
    }

    public String getAccount() {
        return account;
    }

    public Timestamp getTime() {
        return time;
    }

    public Integer getEvent() {
        return event;
    }

    public enum ContentEventType {
        CONTENT_EVENT_CREATE(1),
        CONTENT_EVENT_UPDATE(2),
        CONTENT_EVENT_STAR(3),
        CONTENT_EVENT_UNSTAR(4),
        CONTENT_EVENT_RECOVER(5),
        CONTENT_EVENT_RECYCLE(6),
        CONTENT_EVENT_ARCHIVE(6),
        CONTENT_EVENT_UNARCHIVE(7),
        CONTENT_EVENT_DELETE(8);
        public int type;
        ContentEventType(int type) {
            this.type = type;
        }
    }
}
