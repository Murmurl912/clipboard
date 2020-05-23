package com.example.clipboard.server.entity.temp;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("verification_record")
public class VerificationRecord {
    public String account;
    public Date latest;
}
