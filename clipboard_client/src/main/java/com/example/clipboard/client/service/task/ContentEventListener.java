package com.example.clipboard.client.service.task;

import com.example.clipboard.client.event.ContentEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ContentEventListener implements ApplicationListener<ContentEvent> {
    @Override
    public void onApplicationEvent(ContentEvent contentEvent) {

    }
}
