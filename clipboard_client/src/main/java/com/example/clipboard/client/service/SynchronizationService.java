package com.example.clipboard.client.service;

import com.example.clipboard.client.event.AppEvent;
import com.example.clipboard.client.event.AppStartEvent;
import com.example.clipboard.client.repository.RemoteContentRepository;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;

@Lazy(false)
@Service
public class SynchronizationService implements ApplicationListener<AppEvent> {

    private final RemoteContentRepository remote;
    public SynchronizationService(RemoteContentRepository remote) {
        this.remote = remote;
    }

    @Override
    public void onApplicationEvent(AppEvent event) {
        if(event instanceof AppStartEvent) {
            handle((AppStartEvent)event);
        }
    }

    public void handle(AppStartEvent event) {

    }


}
