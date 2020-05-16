package com.example.clipboard.client.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table
public class Account {
    @Id
    public String id;
    public String username;
    public String token;
    public String email;
    public String avatar;
}
