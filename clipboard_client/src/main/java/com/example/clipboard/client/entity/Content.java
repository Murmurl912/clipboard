package com.example.clipboard.client.entity;

import java.util.Date;

public class Content {

    public String id;
    public String clipboard;
    public String account;
    public ContentStatus status;

    public String device;
    public String deviceType;
    public String deviceOs;

    public String title;
    public String content;
    public Boolean star;
    public Boolean archive;

    public Boolean attached;
    public String attachment;
    public Long size;
    public byte[] binary;

    public Date create;
    public Date update;

    public static enum ContentStatus {
        CONTENT_STATUS_SYNCED,
        CONTENT_STATUS_LOCAL,
        CONTENT_STATUS_SYNCING,
        CONTENT_STATUS_SYNC_FAILED;
    }
}
