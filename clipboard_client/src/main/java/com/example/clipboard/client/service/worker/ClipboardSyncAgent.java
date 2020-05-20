package com.example.clipboard.client.service.worker;

import com.example.clipboard.client.lifecycle.event.ContentEvent;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClipboardSyncAgent implements ApplicationListener<ContentEvent> {

    private final BlockingQueue<ContentEvent> events = new LinkedBlockingQueue<>();

    @Override
    public void onApplicationEvent(ContentEvent event) {
        events.offer(event);
    }


}
