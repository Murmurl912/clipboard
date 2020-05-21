package com.example.clipboard.client.repository.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
