package com.example.clipboard.client;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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

    @Bean("sync")
    TaskExecutor executor() {
        return new ThreadPoolTaskExecutor();
    }
}
