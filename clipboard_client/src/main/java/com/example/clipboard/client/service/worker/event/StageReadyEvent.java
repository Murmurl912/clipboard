package com.example.clipboard.client.service.worker.event;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class StageReadyEvent extends ApplicationEvent {

    public StageReadyEvent(Object source) {
        super(source);
    }

    public Stage getStage() {
        return (Stage) getSource();
    }
}
