package com.example.clipboard.client.service.worker;

import com.example.clipboard.client.service.worker.event.AppEvent;

public class ClipboardRefreshEvent extends AppEvent {
    public ClipboardRefreshEvent(Object source) {
        super(source);
    }
}
