package com.example.clipboard.client.ui;

import com.example.clipboard.client.service.worker.event.StageReadyEvent;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    private final ApplicationContext context;
    @Value("classpath:view/main_view.fxml")
    private Resource main;
    @Value("classpath:image/icon.png")
    private Resource icon;

    public StageInitializer(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
        try {
            Stage stage = stageReadyEvent.getStage();
            // icon
            ObservableList<Image> images = stage.getIcons();
            Image image = new Image(icon.getURL().toExternalForm());
            images.add(image);

            // main view
            Resource view = main;
            stage.initStyle(StageStyle.DECORATED);


            FXMLLoader loader = new FXMLLoader(view.getURL());
            loader.setControllerFactory(context::getBean);
            stage.setScene(new Scene(loader.load()));

            stage.show();
        } catch (Exception e) {
            // ToDo: error handling
            throw new RuntimeException(e);
        }
    }


}
