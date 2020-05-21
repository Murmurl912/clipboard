package com.example.clipboard.client.repository.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class App {
    @Id
    public String id;
    public String username;
    public String account;
    public String token;
    public String email;
    public String avatar;

}
