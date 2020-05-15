package com.example.clipboard.client.lifecycle;

import org.springframework.context.ApplicationEvent;

public class StageSwitchEvent extends ApplicationEvent {
    public StageSwitchEvent(Object source) {
        super(source);
    }

}
