package com.example.clipboard.client.service;

import com.example.clipboard.client.repository.entity.AccessToken;
import com.example.clipboard.client.repository.entity.App;
import org.springframework.stereotype.Component;

@Component
public class AppContext {
    public String id;
    public String username;
    public String account = "test";
    public String token;
    public String email;
    public String avatar;
    public AccessToken access;
    public String baseUrl = "http://localhost:8080";
    public Boolean auto = true;
    public Integer limit = 100;
    public Long period = 1000L;
    public String eventUrl = "/clipboard/account/{account}/event";
}
