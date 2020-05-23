package com.example.clipboard.server.entity.temp;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("access_token")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessToken {
    public String id;
    public String account;
    public String salt;
    public Date create;
    public Date expire;
    public String key;
    public String sign;
}
