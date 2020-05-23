package com.example.clipboard.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Configuration
public class MongoTransactionConfiguration {

    @Bean
    public MongoTransactionManager get(MongoDbFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
