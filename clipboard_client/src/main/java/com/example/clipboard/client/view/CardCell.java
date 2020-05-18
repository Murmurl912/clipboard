package com.example.clipboard.client.view;

import com.example.clipboard.client.entity.Content;
import javafx.scene.Node;
import org.controlsfx.control.GridCell;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


public class CardCell extends GridCell<Content> {

    private final ViewReadyCallback mViewReadyCallback = new ViewReadyCallback() {
        @Override
        public void onReady(CardCell cardCell) {

        }
    };

    private final ViewUpdateCallback mViewUpdateCallback = new ViewUpdateCallback() {
        @Override
        public void onUpdate(CardCell cell, Content content, boolean empty) {

        }
    };

    private final Map<String, Node> holder = new HashMap<>();
    private ViewReadyCallback viewReadyCallback;
    private ViewUpdateCallback viewUpdateCallback;

    public CardCell() {

    }

    public CardCell(ViewReadyCallback callback) {
        this.viewReadyCallback = callback;
    }

    public CardCell(ViewUpdateCallback callback) {
        this.viewUpdateCallback = callback;
    }

    public CardCell(ViewReadyCallback readyCallback,
                    ViewUpdateCallback updateCallback) {
        this.viewUpdateCallback = updateCallback;
        this.viewReadyCallback = readyCallback;
    }

    @PostConstruct
    private void init() {
        if (this.viewReadyCallback == null) {
            this.viewReadyCallback = mViewReadyCallback;
        }

        if (this.viewUpdateCallback == null) {
            this.viewUpdateCallback = mViewUpdateCallback;
        }
    }

    public Map<String, Node> getHolder() {
        return holder;
    }


    public ViewReadyCallback getViewReadyCallback() {
        return viewReadyCallback;
    }

    public void setViewReadyCallback(ViewReadyCallback viewReadyCallback) {
        if (viewReadyCallback != null) {
            this.viewReadyCallback = viewReadyCallback;
        }
    }

    public ViewUpdateCallback getViewUpdateCallback() {
        return viewUpdateCallback;
    }

    public void setViewUpdateCallback(ViewUpdateCallback viewUpdateCallback) {
        if (viewUpdateCallback != null) {
            this.viewUpdateCallback = viewUpdateCallback;
        }
    }

    @Override
    protected void updateItem(Content content, boolean empty) {
        if (getGraphic() == null) {
            viewReadyCallback.onReady(this);
        }
        viewUpdateCallback.onUpdate(this, content, empty);
    }

    public static interface ViewReadyCallback {
        public void onReady(CardCell cardCell);
    }

    public static interface ViewUpdateCallback {
        public void onUpdate(CardCell cell, Content content, boolean empty);
    }

}
