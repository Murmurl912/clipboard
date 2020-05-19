package com.clipboard.clipboard_store.event;

import java.util.Date;

public class ClipboardContentEvent extends ClipboardEvent {

    private final String id;
    private final Integer type;
    private final Date version;

    public ClipboardContentEvent(String id,
                                 String account,
                                 ClipboardContentEventType type,
                                 Date version) {
        super(account);
        this.id = id;
        this.type = type.EVENT;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public Integer getType() {
        return type;
    }

    public Date getVersion() {
        return version;
    }

    @Override
    public String getSource() {
        return (String) super.getSource();
    }


    @Override
    public String toString() {
        return "ClipboardContentEvent{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", version=" + version +
                ", source=" + source +
                '}';
    }

    public static enum ClipboardContentEventType {
        CONTENT_CREATE_EVENT(0),
        CONTENT_UPDATE_EVENT(1),
        CONTENT_STAR_EVENT(2),
        CONTENT_UNSTAR_EVENT(3),
        CONTENT_DELETE_EVENT(4);

        public int EVENT;

        ClipboardContentEventType(int event) {
            this.EVENT = event;
        }
    }
}
