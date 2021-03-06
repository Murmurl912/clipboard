package com.example.clipboard.client.ui.model;

import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.service.AppContext;
import com.example.clipboard.client.service.ClipboardService;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Consumer;

@Component
public class ClipboardModel implements Consumer<Content> {

    private final ObservableList<Content> contents;
    private final ClipboardService service;
    private final AppContext appContext;

    public ClipboardModel(ClipboardService service,
                          AppContext appContext) {
        this.service = service;
        this.appContext = appContext;
        contents = FXCollections.observableArrayList();
        service.subscribe().subscribe(this);
        refresh();
    }

    public void refresh() {
        Platform.runLater(contents::clear);
        service.refresh();
    }

    public ObservableList<Content> clipboard() {
        return contents;
    }

    public void state(String id, Content.ContentState state) {
        service.delete(id)
                .subscribe(content -> {
                    Platform.runLater(() -> {
                        contents.removeIf(data ->
                                Objects.equals(data.id, content.id));
                    });
                });
    }

    public void upload(Content content) {
        service.upload(content);
    }

    public AppContext context() {
        return appContext;
    }

    public void clear() {
        Platform.runLater(contents::clear);
    }

    public FilteredList<Content> search(String key) {
        return contents.filtered(content -> content.content.toLowerCase().contains(key.toLowerCase()));
    }

    public ObservableList<Content> search(ObservableValue<String> key) {
        ObservableList<Content> contents = FXCollections.observableArrayList();
        key.addListener((value, before, after) -> {
            FilteredList<Content> list = search(value.getValue());
            Platform.runLater(()->{
                contents.removeAll();
                contents.addAll(list);
            });
        });
        return contents;
    }

    @Override
    public void accept(Content content) {
        System.out.println(content);
        remove(content);
        add(content);
    }

    private void remove(Content content) {
        Platform.runLater(() -> {
            contents.removeIf(item -> Objects.equals(item.id, content.id)
                    || Objects.equals(item.uuid, content.uuid));
        });
    }

    private void add(Content content) {
        if (content.state == Content.ContentState.CONTENT_STATE_NORMAL.STATE) {
            Platform.runLater(() -> {
                contents.add(0, content);
                contents.sort((a, b) -> -a.update.compareTo(b.update));
            });
        }
    }
}
