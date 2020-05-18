package com.clipboard.clipboard_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class ClipboardStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClipboardStoreApplication.class, args);
    }


    @Lazy
    @Bean("SHA1")
    MessageDigest digest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA1");
    }

}
