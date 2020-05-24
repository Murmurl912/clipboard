package com.clipboard.clipboard_store.endpoint.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("access_token")
public class AccessToken {
    public String id;
    public String account;
    public Date create;
    public Date expire;
    public String key;
    public String sign;
}
