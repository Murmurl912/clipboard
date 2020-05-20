package com.example.clipboard.client.service;

import com.example.clipboard.client.lifecycle.event.AppEvent;
import com.example.clipboard.client.lifecycle.event.AppStartEvent;
import com.example.clipboard.client.repository.RemoteContentRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy(false)
@Service
public class SynchronizationService implements ApplicationListener<AppEvent> {

    private final RemoteContentRepository remote;
    public SynchronizationService(RemoteContentRepository remote) {
        this.remote = remote;
    }

    @Override
    public void onApplicationEvent(AppEvent event) {

    }

    public void handle(AppStartEvent event) {

    }


}
