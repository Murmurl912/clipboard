package com.example.clipboard.client;

import com.example.clipboard.client.service.worker.event.AppStartEvent;
import com.example.clipboard.client.service.worker.event.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class FxApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Stage Ready At: " + System.currentTimeMillis());
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void init() throws Exception {
        String[] args = getParameters().getRaw().toArray(new String[0]);
        this.applicationContext = new SpringApplicationBuilder()
                .sources(ClipboardClientApplication.class)
                .run(args);
        applicationContext.publishEvent(new AppStartEvent(this));

    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }
}
