package com.example.clipboard.client.service.worker;

import com.example.clipboard.client.lifecycle.event.AppEvent;
import com.example.clipboard.client.lifecycle.event.AppStartEvent;
import com.example.clipboard.client.lifecycle.event.StartAgentEvent;
import com.example.clipboard.client.service.worker.event.AgentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy(false)
@Service
public class ClipboardTrigger implements ApplicationListener<AppEvent> {

    @Autowired
    public ApplicationEventPublisher publisher;

    @Override
    public void onApplicationEvent(AppEvent appEvent) {
        if(appEvent instanceof AppStartEvent) {
            publisher.publishEvent(new AgentEvent(this, AgentEvent.AgentEventType.START_ALL));
        }
    }
}
