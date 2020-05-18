package com.clipboard.clipboard_store.repository.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.sql.Timestamp;
import java.util.Date;

@Document(value = "clipboard_content")
public class ClipboardContent {
    @MongoId
    public String id;
    public String account;

    public String content;
    public byte[] hash;
    @Field(value = "content_version")
    public Timestamp contentVersion;

    public Boolean star;
    @Field(value = "content_version")
    public Timestamp starVersion;

    public Integer state;
    @Field(value = "content_version")
    public Timestamp stateVersion;

    public Timestamp create;
    public Timestamp update;

    public static enum ContentState {
        CONTENT_STATE_NORMAL(0),
        CONTENT_STATE_ARCHIVE(1),
        CONTENT_STATE_RECYCLE(2),
        CONTENT_STATE_DELETE(3);

        public int STATE;
        ContentState(int state) {
            STATE = state;
        }

        public static ContentState get(int state) {
            switch (state) {
                case 1: return CONTENT_STATE_ARCHIVE;
                case 2: return CONTENT_STATE_RECYCLE;
                case 3: return CONTENT_STATE_DELETE;
                default: return CONTENT_STATE_NORMAL;
            }
        }
    }
}
