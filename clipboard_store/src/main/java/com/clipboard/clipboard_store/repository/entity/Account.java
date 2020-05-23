package com.clipboard.clipboard_store.repository.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class Account {
    @Id
    public String id;
    @Indexed(unique = true)
    public String username;
    @Indexed(unique = true)
    public String email;
    public String password;
    public Date create;
    public Date update;
}
