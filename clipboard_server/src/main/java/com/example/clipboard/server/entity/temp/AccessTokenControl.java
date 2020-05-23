package com.example.clipboard.server.entity.temp;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("access_token_control")
public class AccessTokenControl {
    @Indexed(unique = true)
    public String token;
}
