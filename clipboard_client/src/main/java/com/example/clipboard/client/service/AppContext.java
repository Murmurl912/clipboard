package com.example.clipboard.client.service;

import org.springframework.stereotype.Component;

@Component
public class AppContext {
    public String id;
    public String username;
    public String account;
    public String token;
    public String email;
    public String baseUrl = "http://localhost:8080";
    public Boolean auto = false;
    public Integer limit = 100;
    public Long period = 1000L;
    public String eventUrl = "/clipboard/account/{account}/event";
}
