package com.example.clipboard.client.service.worker;

import java.util.Date;

public class ClipboardEventModel {

    public String source;
    public String id;
    public Integer type;
    public Date version;
    public Date timestamp;

    public static enum ClipboardContentEventType {
        CONTENT_CREATE_EVENT(0),
        CONTENT_STATE_EVENT(1);
        public int EVENT;

        ClipboardContentEventType(int event) {
            this.EVENT = event;
        }
    }
}
