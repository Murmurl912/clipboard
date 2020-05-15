package com.example.clipboard.server.entity;

import java.util.Date;

public class Content {
    public String id;
    public String account;

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
}
