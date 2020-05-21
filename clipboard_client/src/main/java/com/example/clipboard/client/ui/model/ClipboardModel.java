package com.example.clipboard.client.ui.model;

import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.service.AppContext;
import com.example.clipboard.client.service.ReactiveClipboardService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Consumer;

@Component
public class ClipboardModel implements Consumer<Content> {

    private final ObservableList<Content> contents;
    private final ObservableList<Content> stars;
    private final ReactiveClipboardService service;
    private final AppContext appContext;

    public ClipboardModel(ReactiveClipboardService service,
                          AppContext appContext) {
        this.service = service;
        this.appContext = appContext;
        contents = FXCollections.observableArrayList();
        stars = FXCollections.observableArrayList();
        service.subscribe().subscribe(this);
        refresh();
    }

    public void refresh() {
        service.refresh();
    }

    public ObservableList<Content> clipboard() {
        return contents;
    }

    public ObservableList<Content> star() {
        return stars;
    }


    public void star(String id, boolean star) {
        service.star(id, star)
                .subscribe(content -> {
                    Platform.runLater(()->{
                        contents.removeIf(data ->
                                Objects.equals(data.id, content.id));
                        stars.removeIf(data ->
                                Objects.equals(data.id, content.id));
                        contents.add(0, content);
                        if(content.star) {
                            stars.add(0, content);
                        }
                    });
                });
    }

    public void state(String id, Content.ContentState state) {
        service.state(id, state)
                .subscribe(content -> {
                    Platform.runLater(()->{
                        contents.removeIf(data ->
                                Objects.equals(data.id, content.id));
                        stars.removeIf(data ->
                                Objects.equals(data.id, content.id));
                        if(content.state ==
                                Content.ContentState.CONTENT_STATE_DELETE.STATE) {
                            return;
                        }
                        contents.add(0, content);
                        if(content.star) {
                            stars.add(0, content);
                        }
                    });
                });
    }

    @Override
    public void accept(Content content) {
        System.out.println(content);
        remove(content);
        add(content);
    }

    private void remove(Content content) {
        Platform.runLater(()->{
            contents.removeIf(item -> Objects.equals(item.id, content.id));
            stars.removeIf(item -> Objects.equals(item.id, content.id));
        });
    }

    private void add(Content content) {
        if(content.state == Content.ContentState.CONTENT_STATE_NORMAL.STATE) {
            Platform.runLater(()->{
                contents.add(0, content);
                if(content.star) {
                    stars.add(0, content);
                }
            });
        }
    }
}
