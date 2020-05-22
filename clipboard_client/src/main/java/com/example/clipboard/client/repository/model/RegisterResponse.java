package com.example.clipboard.client.repository.model;

import org.springframework.data.annotation.Id;

import java.util.Date;

public class RegisterResponse {
    public String id;
    public String username;
    public String email;
    public Date create;
    public Date update;
}
