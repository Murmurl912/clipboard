package com.example.clipboard.client;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class ClipboardClientApplication {

    public static void main(String[] args) {
        System.out.println("Application Start At: " + System.currentTimeMillis());
        Application.launch(FxApplication.class, args);
    }

}
