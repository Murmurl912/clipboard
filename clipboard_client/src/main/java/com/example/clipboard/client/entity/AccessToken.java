package com.example.clipboard.client.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "access_token")
public class AccessToken {
    @Id
    public String id;
    public String account;
    public String salt;
    public Date create;
    public Date expire;
    public String key;
    public String sign;
}
