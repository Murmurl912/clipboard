package com.example.clipboard.client.entity;

import java.util.Date;

public class AccessToken {

    public String id;
    public String account;
    public String salt;
    public Date create;
    public Date expire;
    public String key;
    public String sign;
}
