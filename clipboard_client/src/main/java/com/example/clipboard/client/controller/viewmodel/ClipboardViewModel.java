package com.example.clipboard.client.controller.viewmodel;

import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.ContentEvent;
import com.example.clipboard.client.event.content.*;
import com.example.clipboard.client.lifecycle.ApplicationInfo;
import com.example.clipboard.client.service.CachedClipboardService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

//todo need change order

/**
 * this is where clipboard's content store in memory
 */
@Component
public class ClipboardViewModel implements ApplicationListener<ContentEvent> {

    private final ObservableList<Content> clipboard = FXCollections.observableArrayList();
    private final AtomicBoolean clipboardRefreshing = new AtomicBoolean(false);
    private final ObservableList<Content> star = FXCollections.observableArrayList();
    private final AtomicBoolean starRefreshing = new AtomicBoolean(false);
    private final ObservableList<Content> archive = FXCollections.observableArrayList();
    private final AtomicBoolean archiveRefreshing = new AtomicBoolean(false);
    private final ObservableList<Content> trash = FXCollections.observableArrayList();
    private final AtomicBoolean trashRefreshing = new AtomicBoolean(false);

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
        if(clipboardRefreshing.get()) {
            return;
        }
        clipboardRefreshing.set(true);
        CachedClipboardService service = context.getBean(CachedClipboardService.class);
        Disposable subscribe = Single.fromCallable(service::getClipboard)
                .subscribeOn(Schedulers.from(context.getBean("taskScheduler", Executor.class)))
                .subscribe(contents -> {
                    clipboard.addAll(contents);
                    clipboardRefreshing.set(false);
                });
    }

    public void refreshStar() {
        if(starRefreshing.get()) {
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

    public void refreshArchive() {
        if(archiveRefreshing.get()) {
            return;
        }
        archiveRefreshing.set(true);
        CachedClipboardService service = context.getBean(CachedClipboardService.class);
        Disposable subscribe = Single.fromCallable(service::getArchive)
                .subscribeOn(Schedulers.from(context.getBean("taskScheduler", Executor.class)))
                .subscribe(contents -> {
                    archive.clear();
                    archive.addAll(contents);
                    archiveRefreshing.set(false);
                });
    }

    public void refreshRecycle() {
        if(trashRefreshing.get()) {
            return;
        }
        trashRefreshing.set(true);
        CachedClipboardService service = context.getBean(CachedClipboardService.class);
        Disposable subscribe = Single.fromCallable(service::getRecycle)
                .subscribeOn(Schedulers.from(context.getBean("taskScheduler", Executor.class)))
                .subscribe(contents -> {
                    trash.clear();
                    trash.addAll(contents);
                    trashRefreshing.set(false);
                });
    }


    // todo implement
    public void refreshClipboardIfNecessary() {

    }

    public void refreshStarIfNecessary() {

    }

    public void refreshArchiveIfNecessary() {

    }

    public void refreshTrashIfNecessary() {

    }

    @Override
    public void onApplicationEvent(ContentEvent contentEvent) {
        // handle content change
        // reflect to in memory cache
        if(contentEvent instanceof ContentUpdateEvent) {
            handle((ContentUpdateEvent) contentEvent);
        }
        if(contentEvent instanceof ContentDeleteEvent) {
            handle((ContentDeleteEvent) contentEvent);
        }
        if(contentEvent instanceof ContentRecycleEvent) {
            handle((ContentRecycleEvent) contentEvent);
        }
        if(contentEvent instanceof ContentArchiveEvent) {
            handle((ContentArchiveEvent) contentEvent);
        }
        if(contentEvent instanceof ContentStarEvent) {
            handle((ContentStarEvent) contentEvent);
        }
        if(contentEvent instanceof ContentCreateEvent) {
            handle((ContentCreateEvent) contentEvent);
        }

    }

    /**
     * this implementation assume archived or recycled content
     * will not be updated
     * @param event event
     */
    private void handle(@NonNull ContentUpdateEvent event) {
        switch (Content.ContentState.get(event.getBefore().state)) {
            case CONTENT_STATE_NORMAL:
                clipboard.remove(event.getBefore());
                clipboard.add(0, event.getNow());
                break;
            case CONTENT_STATE_STAR:
                clipboard.remove(event.getBefore());
                clipboard.add(0, event.getNow());
                star.remove(event.getBefore());
                star.add(0, event.getNow());
                break;
            case CONTENT_STATE_ARCHIVE:
            case CONTENT_STATE_RECYCLE:
            case CONTENT_STATE_DELETE:
            default: throw new IllegalStateException("Cannot update a deleted, archived, recycled content");
        }
    }

    /**
     * remove content
     * @param event event
     */
    private void handle(@NonNull ContentDeleteEvent event) {
        switch (Content.ContentState.get(event.getBefore().state)) {
            case CONTENT_STATE_NORMAL:
                clipboard.remove(event.getBefore());
                break;
            case CONTENT_STATE_STAR:
                clipboard.remove(event.getBefore());
                star.remove(event.getBefore());
                break;
            case CONTENT_STATE_ARCHIVE:
                archive.remove(event.getBefore());
            case CONTENT_STATE_RECYCLE:
                trash.remove(event.getBefore());
                break;
            case CONTENT_STATE_DELETE:
            default: throw new IllegalStateException("Cannot delete a deleted content");

        }
    }

    /**
     *
     * @param event event
     */
    private void handle(@NonNull ContentArchiveEvent event) {
        switch (Content.ContentState.get(event.getBefore().state)) {
            case CONTENT_STATE_NORMAL:
                clipboard.remove(event.getBefore());
                archive.add(event.getNow());
                break;
            case CONTENT_STATE_STAR:
                clipboard.remove(event.getBefore());
                star.remove(event.getBefore());
                archive.add(event.getNow());
                break;
            case CONTENT_STATE_RECYCLE:
                trash.remove(event.getBefore());
                archive.add(event.getNow());
                break;
            case CONTENT_STATE_ARCHIVE:
            case CONTENT_STATE_DELETE:
            default: throw new IllegalStateException("Cannot archive a deleted or an archived content");
        }
    }

    private void handle(@NonNull ContentStarEvent event) {
        switch (Content.ContentState.get(event.getBefore().state)) {
            case CONTENT_STATE_NORMAL:
                clipboard.remove(event.getBefore());
                clipboard.add(event.getNow());
                star.add(event.getNow());
                break;
            case CONTENT_STATE_ARCHIVE:
                archive.remove(event.getBefore());
                clipboard.add(event.getNow());
                star.add(event.getNow());
            case CONTENT_STATE_RECYCLE:
                trash.remove(event.getBefore());
                clipboard.add(event.getNow());
                star.add(event.getNow());
            case CONTENT_STATE_STAR:
            case CONTENT_STATE_DELETE:
            default: throw new IllegalStateException("Cannot star a stared or deleted content");
        }
    }

    private void handle(@NonNull ContentRecycleEvent event) {
        switch (Content.ContentState.get(event.getNow().state)) {
            case CONTENT_STATE_NORMAL:
                clipboard.remove(event.getBefore());
                trash.add(event.getNow());
                break;
            case CONTENT_STATE_STAR:
                clipboard.remove(event.getBefore());
                star.remove(event.getBefore());
                trash.add(event.getNow());
                break;
            case CONTENT_STATE_ARCHIVE:
                archive.remove(event.getBefore());
                trash.add(event.getNow());
            case CONTENT_STATE_RECYCLE:
            case CONTENT_STATE_DELETE:
            default: throw new IllegalStateException("Cannot recycle a recycled or deleted content");
        }
    }


    private void handle(@NonNull ContentCreateEvent event) {

    }


}
