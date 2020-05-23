package com.example.clipboard.client.service.worker;

import com.example.clipboard.client.service.worker.event.AgentEvent;
import com.example.clipboard.client.service.worker.event.AgentStatusChangeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Date;

@Lazy(false)
@Service
public class ClipboardGuard implements ApplicationListener<AgentStatusChangeEvent> {

    private final ApplicationEventPublisher publisher;
    private final TaskScheduler scheduler;
    private double retry = 1;

    public ClipboardGuard(ApplicationEventPublisher publisher,
                          TaskScheduler taskScheduler) {
        this.publisher = publisher;
        this.scheduler = taskScheduler;
    }

    @Override
    public void onApplicationEvent(AgentStatusChangeEvent event) {
        switch (event.getSource()) {
            case CONNECTION_LOST:
                scheduler.schedule(() -> {
                    publisher.publishEvent(new AgentEvent(this,
                            AgentEvent.AgentEventType.START_CLOUD_AGENT));
                }, new Date(System.currentTimeMillis() + retry()));
                break;
            case CONNECTION_ALIVE:
                retry = 1;
                break;
        }
    }

    private long retry() {
        retry = 1.1 * retry;
        if (retry > 60) {
            retry = 60;
        }
        return (long) (retry * 500);
    }

}
