package com.clipboard.clipboard_store.repository.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(value = "clipboard_content")
public class ClipboardContent {
    public String id;
    public String account;

    public String content;
    public byte[] hash;
    @Field(value = "content_version")
    public Date contentVersion;

    public Boolean star;
    @Field(value = "star_version")
    public Date starVersion;

    public Integer state;
    @Field(value = "state_version")
    public Date stateVersion;

    public Date create;
    public Date update;

    public static enum ContentState {
        CONTENT_STATE_NORMAL(0),
        CONTENT_STATE_DELETE(1);

        public int STATE;
        ContentState(int state) {
            STATE = state;
        }

        public static ContentState get(int state) {
            if (state == 1) {
                return CONTENT_STATE_DELETE;
            }
            return CONTENT_STATE_NORMAL;
        }
    }
}
