package com.example.clipboard.client.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Scope("prototype")
@Component
public class CardCell extends GridCell<CardCell.CellModel> {

    public Label label;

    @Value("classpath:view/card_cell.fxml")
    private Resource resource;

    private FXMLLoader loader = null;
    public CardCell() {

    }

    @Override
    protected void updateItem(CellModel cellModel, boolean empty) {
        super.updateItem(cellModel, empty);
        if(cellModel == null || empty) {
            return;
        }

        if(loader != null && label != null) {
            label.setText(cellModel.label);
            return;
        }

        try {
            loader = new FXMLLoader(resource.getURL());
            loader.setController(this);
            Node node = loader.load();
            setGraphic(node);
            label.setText(cellModel.label);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void initialize() {

    }

    public static class CellModel {
        public String label;

        public CellModel(String label) {
            this.label = label;
        }

    }
}
