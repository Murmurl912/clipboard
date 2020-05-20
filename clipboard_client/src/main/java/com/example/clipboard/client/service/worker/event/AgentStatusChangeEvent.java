package com.example.clipboard.client.service.worker.event;

import com.example.clipboard.client.lifecycle.event.AppEvent;

public class AgentStatusChangeEvent extends AppEvent {
    public AgentStatusChangeEvent(EventType type) {
        super(type);
    }

    @Override
    public EventType getSource() {
        return (EventType)super.getSource();
    }

    public static enum EventType {
        CONNECTION_LOST(1),
        CONNECTION_ALIVE(2);

        public int TYPE;

        EventType(int type) {
            this.TYPE = type;
        }

    }
}
