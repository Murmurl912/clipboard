package com.example.clipboard.client.service.task;

import com.example.clipboard.client.event.ClipboardUpdateEvent;
import com.example.clipboard.client.lifecycle.ApplicationStartEvent;
import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

// todo implement a cached clipboard content queue for fast content comparision

@Lazy(false)
@Component
public class ClipboardSpyTask implements ApplicationListener<ApplicationStartEvent> {

    // todo replace with a queue
    private final AtomicReference<String> before = new AtomicReference<>(null);

    private final ApplicationEventPublisher publisher;

    public ClipboardSpyTask(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onApplicationEvent(ApplicationStartEvent applicationStartEvent) {
        // todo do something on application start
    }


    @Scheduled(fixedDelay = 1000)
    public void checkClipboard() {
        Platform.runLater(() -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();

            if (clipboard.hasString()) {
                String now = clipboard.getString();
                if (before.get() == null) {
                    before.set(now);
                } else {
                    if (!before.get().equals(now)) {
                        before.set(now);
                        ClipboardUpdateEvent event = new ClipboardUpdateEvent(this, now);
                        publisher.publishEvent(event);
                    }
                }
            }
            ;
        });
    }
}
