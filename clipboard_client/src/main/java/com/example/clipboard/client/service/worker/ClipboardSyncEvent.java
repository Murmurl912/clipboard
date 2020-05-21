package com.example.clipboard.client.service.worker;

import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.service.worker.event.AppEvent;

public class ClipboardSyncEvent extends AppEvent {

    private final Content content;
    public ClipboardSyncEvent(String id, Content content) {
        super(id);
        this.content = content;
    }

    @Override
    public String getSource() {
        return (String)super.getSource();
    }

    public Content getContent() {
        return content;
    }
}
