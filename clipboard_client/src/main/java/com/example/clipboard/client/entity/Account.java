package com.example.clipboard.client.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table
@Entity
public class Account {

    @Id
    public String id;
    public String username;
    public String token;
    public String email;
    public String avatar;
}
