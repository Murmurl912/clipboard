package com.example.clipboard.client.ui.model;

import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.lifecycle.event.ContentEvent;
import com.example.clipboard.client.service.ClipboardService;
import io.reactivex.rxjava3.core.Single;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

//todo need change order

/**
 * this is where clipboard's content store in memory
 */
@Component
public class ClipboardViewModel implements ApplicationListener<ContentEvent> {
    private final Logger logger = LoggerFactory.getLogger(ClipboardViewModel.class);

    private final ObservableList<Content> clipboard = FXCollections.observableArrayList();
    private final AtomicBoolean clipboardRefreshing = new AtomicBoolean(false);
    private final ObservableList<Content> star = FXCollections.observableArrayList();
    private final AtomicBoolean starRefreshing = new AtomicBoolean(false);

    private final ApplicationContext context;

    public ClipboardViewModel(ApplicationContext context) {
        this.context = context;
    }

    public ObservableList<Content> getClipboard() {
        SortedList<?> sortedList =  clipboard.sorted();
        return clipboard;
    }

    public ObservableList<Content> getStar() {
        return star;
    }


    public void refreshClipboard() {
        if (clipboardRefreshing.get()) {
            return;
        }
        ClipboardService service = context.getBean(ClipboardService.class);
        service.clipboard();
    }

    public void refreshStar() {
        if (starRefreshing.get()) {
            return;
        }
        ClipboardService service = context.getBean(ClipboardService.class);
        service.stars();
    }

    public void state(String id, Content.ContentState state) {
        context.getBean(ClipboardService.class)
                .state(id, state, new Date());
    }

    public void star(String id, boolean star) {
        context.getBean(ClipboardService.class)
                .star(id, star, new Date());
    }

    public void text(String id, String content) {
        context.getBean(ClipboardService.class)
                .text(id, content, new Date());
    }

    @Override
    public void onApplicationEvent(ContentEvent contentEvent) {
        // handle content change
        // reflect to in memory cache
        remove(contentEvent.getContent());
        add(contentEvent.getContent());
    }

    private void remove(@NonNull Content content) {
        clipboard.removeIf(data -> (data.id.equals(content.id) || content.content.equals(data.content)));
        star.removeIf(data -> (data.id.equals(content.id) || content.content.equals(data.content)));
    }

    private void add(@NonNull Content content) {
        if(content.state == Content.ContentState.CONTENT_STATE_DELETE.STATE) {
            return;
        }
        clipboard.add(0, content);
        if(content.star) {
            star.add(0, content);
        }
    }
}
