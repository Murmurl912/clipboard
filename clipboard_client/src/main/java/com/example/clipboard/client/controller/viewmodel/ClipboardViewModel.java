package com.example.clipboard.client.controller.viewmodel;

import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.ContentEvent;
import com.example.clipboard.client.service.CachedClipboardService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
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
        return clipboard;
    }

    public ObservableList<Content> getStar() {
        return star;
    }


    public void refreshClipboard() {
        if (clipboardRefreshing.get()) {
            return;
        }
        clipboardRefreshing.set(true);
        CachedClipboardService service = context.getBean(CachedClipboardService.class);
        Disposable subscribe = Single.fromCallable(service::getClipboard)
                .subscribeOn(Schedulers.from(context.getBean("taskScheduler", Executor.class)))
                .subscribe(contents -> {
                    clipboard.clear();
                    clipboard.addAll(contents);
                    clipboardRefreshing.set(false);
                });
    }

    public void refreshStar() {
        if (starRefreshing.get()) {
            return;
        }
        starRefreshing.set(true);
        CachedClipboardService service = context.getBean(CachedClipboardService.class);
        Disposable subscribe = Single.fromCallable(service::getStar)
                .subscribeOn(Schedulers.from(context.getBean("taskScheduler", Executor.class)))
                .subscribe(contents -> {
                    star.clear();
                    star.addAll(contents);
                    starRefreshing.set(false);
                });
    }


    // todo implement
    public void refreshClipboardIfNecessary() {

    }

    public void refreshStarIfNecessary() {

    }


    public Single<Content> state(String id, Content.ContentState state) {
        return Single.fromCallable(() -> {
            return context.getBean(CachedClipboardService.class)
                    .state(id, state);
        });
    }

    public Single<Content> star(String id, boolean star) {
        return Single.fromCallable(() -> {
            return context.getBean(CachedClipboardService.class)
                    .star(id, star);
        });
    }

    public Single<Content> text(String id, String content) {
        return Single.fromCallable(() -> {
            return context.getBean(CachedClipboardService.class)
                    .text(id, content);
        });
    }

    @Override
    public void onApplicationEvent(ContentEvent contentEvent) {
        // handle content change
        // reflect to in memory cache
        remove(contentEvent.getBefore());
        add(contentEvent.getNow());
    }

    private void remove(@NonNull Content content) {
        clipboard.remove(content);
        star.remove(content);
    }

    private void add(@NonNull Content content) {
        switch (Content.ContentState.get(content.state)) {
            case CONTENT_STATE_NORMAL:
                clipboard.add(0, content);
                if (content.star) {
                    star.add(0, content);
                }
                break;
            case CONTENT_STATE_DELETE:
            default:
        }
    }

}
