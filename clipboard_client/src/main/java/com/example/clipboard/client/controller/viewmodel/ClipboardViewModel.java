package com.example.clipboard.client.controller.viewmodel;

import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.ContentEvent;
import com.example.clipboard.client.lifecycle.ApplicationInfo;
import com.example.clipboard.client.service.CachedClipboardService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

//todo need change order

@Component
public class ClipboardViewModel implements ApplicationListener<ContentEvent> {

    private final ObservableList<Content> clipboard = FXCollections.observableArrayList();
    private final AtomicBoolean clipboardRefreshing = new AtomicBoolean(false);
    private final ObservableList<Content> star = FXCollections.observableArrayList();
    private final ObservableList<Content> archive = FXCollections.observableArrayList();
    private final ObservableList<Content> trash = FXCollections.observableArrayList();

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

    public ObservableList<Content> getArchive() {
        return archive;
    }

    public ObservableList<Content> getTrash() {
        return trash;
    }

    public void refreshClipboard() {
        clipboardRefreshing.set(true);
        CachedClipboardService service = context.getBean(CachedClipboardService.class);
        Disposable subscribe = Single.fromCallable(service::gets)
                .subscribeOn(Schedulers.from(context.getBean("taskScheduler", Executor.class)))
                .subscribe(contents -> {

                    clipboard.addAll(contents);
                    clipboardRefreshing.set(false);
                    ApplicationInfo applicationInfo = context.getBean(ApplicationInfo.class);
                    applicationInfo.memoryTimestamp.set(System.currentTimeMillis());
                });
    }

    public void refreshClipboardIfNecessary() {
        ApplicationInfo applicationInfo =
                context.getBean(ApplicationInfo.class);
        if (clipboardRefreshing.get()) {
            return;
        }

        if (applicationInfo.cacheTimestamp.get() == -1) {
            refreshClipboard();
        } else if (applicationInfo.cacheTimestamp.get() != applicationInfo.memoryTimestamp.get()) {
            // todo implement minimal update
            refreshClipboard();
        }
    }


    @Override
    public void onApplicationEvent(ContentEvent contentEvent) {
        // handle content change
        // reflect to in memory cache
    }

    private void update(Content content,
                        Content now,
                        ObservableList<Content> contents) {
        if (now.archive) {
            contents.remove(content);
            contents.add(now);
            contents.sort(Comparator.comparing(o -> o.update));
        } else if (now.star) {
            star.add(now);
            star.sort(Comparator.comparing(o -> o.update));
        } else if (now.recycle) {
            star.remove(content);
            clipboard.remove(content);
            contents.remove(content);
            trash.add(now);
            trash.sort(Comparator.comparing(o -> o.update));
        }
    }
}
