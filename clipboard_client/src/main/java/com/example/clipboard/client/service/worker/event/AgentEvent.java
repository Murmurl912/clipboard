package com.example.clipboard.client.service.worker.event;

import org.springframework.context.ApplicationEvent;

public class AgentEvent extends ApplicationEvent {

    private final AgentEventType type;

    public AgentEvent(Object source, AgentEventType type) {
        super(source);
        this.type = type;
    }

    public AgentEventType getType() {
        return type;
    }

    public static enum AgentEventType {
        START_LOCAL_AGENT(1),
        START_CLOUD_AGENT(2),
        START_ALL(3),
        STOP_LOCAL_AGENT(4),
        STOP_CLOUD_AGENT(5),
        STOP_ALL(6);
        public int TYPE;
        AgentEventType(int type) {
            TYPE = type;
        }

    }
}
