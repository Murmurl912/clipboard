package com.example.clipboard.client.service.task;

import com.example.clipboard.client.event.ClipboardEvent;
import com.example.clipboard.client.lifecycle.ApplicationStartEvent;
import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class ClipboardListener implements ApplicationListener<ApplicationStartEvent> {

    private final AtomicReference<String> before = new AtomicReference<>(null);

    @Autowired
    private ApplicationEventPublisher publisher;

    @Override
    public void onApplicationEvent(ApplicationStartEvent applicationStartEvent) {
        System.out.println("event: " + applicationStartEvent);
    }

    @Scheduled(fixedDelay = 1000 )
    public void checkClipboard() {
        Platform.runLater(()->{

            Clipboard clipboard = Clipboard.getSystemClipboard();

            if(clipboard.hasString()) {
                String now = clipboard.getString();
                if(before.get() == null) {
                    before.set(now);
                    System.out.println("Changed: " + now);
                } else {
                    if(!before.get().equals(now)) {
                        before.set(now);
                        ClipboardEvent event = new ClipboardEvent(this, now);
                        publisher.publishEvent(event);
                        System.out.println("Changed: " + now);
                    };
                }
            };
        });
    }
}
