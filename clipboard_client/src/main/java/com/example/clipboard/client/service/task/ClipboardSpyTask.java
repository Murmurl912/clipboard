package com.example.clipboard.client.service.task;

import com.example.clipboard.client.event.ClipboardEvent;
import com.example.clipboard.client.event.clipboard.ClipboardUpdateEvent;
import com.example.clipboard.client.event.AppStartEvent;
import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

// todo implement a cached clipboard content queue for fast content comparision

@Lazy(false)
@Component
public class ClipboardSpyTask implements ApplicationListener<AppStartEvent> {

    // todo replace with a queue
    private final AtomicReference<String> before = new AtomicReference<>(null);

    private final ApplicationEventPublisher publisher;

    public ClipboardSpyTask(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onApplicationEvent(AppStartEvent applicationStartEvent) {
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
                        ClipboardEvent event = new ClipboardUpdateEvent(this, now);
                        publisher.publishEvent(event);
                    }
                }
            }
        });
    }
}
