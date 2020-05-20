package com.example.clipboard.client;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@EnableScheduling
@SpringBootApplication
public class ClipboardClientApplication {

    public static void main(String[] args) {
        System.out.println("Application Start At: " + System.currentTimeMillis());
        Application.launch(FxApplication.class, args);
    }

    @Lazy
    @Bean("SHA1")
    MessageDigest digest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA1");
    }

    @Bean("ABC")
    DataSource dataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:file:./app");
        dataSourceBuilder.username("sa");
        dataSourceBuilder.password("password");
        return dataSourceBuilder.build();
    }

}
