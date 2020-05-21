package com.example.clipboard.client.ui.model;

import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.service.ReactiveClipboardService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import net.rgielen.fxweaver.core.FxContextLoader;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Objects;

@Component
public class ClipboardModel {

    private ObservableList<Content> contents;
    private ObservableList<Content> stars;
    private final ReactiveClipboardService service;

    public ClipboardModel(ReactiveClipboardService service) {
        this.service = service;
        contents = FXCollections.observableArrayList();
        contents = contents.sorted((a, b) -> a.update.compareTo(b.update));
        stars = FXCollections.observableArrayList();
        stars = stars.sorted((a, b) -> a.update.compareTo(b.update));
    }

    public ObservableList<Content> clipboard() {
        service.clipboard()
                .filter(content -> content.state ==
                        Content.ContentState.CONTENT_STATE_DELETE.STATE)
                .subscribe(content -> {
                    Platform.runLater(()->{
                        if(contents.contains(content)) {
                            return;
                        }
                        contents.removeIf(data ->
                                Objects.equals(data.id, content.id));
                        contents.add(content);
                    });
                });
        return contents;
    }

    public ObservableList<Content> star() {
        service.star(true)
                .filter(content -> content.state ==
                        Content.ContentState.CONTENT_STATE_DELETE.STATE)
                .subscribe(content -> {
                    Platform.runLater(()->{
                        if(stars.contains(content)) {
                            return;
                        }
                        stars.removeIf(data ->
                                Objects.equals(data.id, content.id));
                        stars.add(content);
                    });
                });
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


}
