package com.example.clipboard.client.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table
@Entity
public class Clipboard {
    @Id
    public String name;
    public Date checkpoint;
}
