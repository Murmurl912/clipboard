package com.example.clipboard.client.service;

import com.example.clipboard.client.service.worker.event.ClipboardEvent;
import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.lifecycle.event.content.*;
import com.example.clipboard.client.repository.CachedContentRepository;
import com.example.clipboard.client.repository.RemoteContentRepository;
import com.example.clipboard.client.repository.model.ContentCreateModel;
import com.example.clipboard.client.repository.model.ContentStarModel;
import com.example.clipboard.client.repository.model.ContentStateModel;
import com.example.clipboard.client.repository.model.ContentTextModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.UUID;

@Service
public class ClipboardService implements ApplicationListener<ClipboardEvent> {

    private final RemoteContentRepository remote;
    private final CachedContentRepository cached;
    private final ApplicationEventPublisher publisher;
    private final MessageDigest digest;
    private final String account = "test";
    @Autowired
    private ReactiveClipboardService service;
    public ClipboardService(RemoteContentRepository remote,
                            CachedContentRepository cached,
                            ApplicationEventPublisher publisher, 
                            MessageDigest digest) {

        this.remote = remote;
        this.cached = cached;
        this.publisher = publisher;
        this.digest = digest;
    }
    
    
    public void create(String text) {
        if(StringUtils.isEmpty(text)) {
            throw new RuntimeException();
        }
        String uuid = UUID.randomUUID().toString();
        ContentCreateModel model = createModel(text, uuid, account);

        remote.create(model)
                .subscribe(data -> {
                    ContentCreateEvent event = new ContentCreateEvent(data.id, data);
                    publisher.publishEvent(event);
                });
    }

    
    public void star(String id,
                        boolean star,
                        Date starVersion) {
        remote.star(id, createContentStarModel(star, starVersion))
                .subscribe(content -> {
                    ContentStarEvent event = new ContentStarEvent(content.id, content);
                    publisher.publishEvent(event);
                });
    }

    
    public void state(String id,
                      Content.ContentState state,
                      Date stateVersion) {
        remote.state(id, createContentStateModel(state, stateVersion))
                .subscribe(content -> {
                    ContentStateEvent event = new ContentStateEvent(content.id, content);
                    publisher.publishEvent(event);
                });
    }

    
    public void text(String id,
                     String text,
                     Date contentVersion) {
        remote.text(id, createContentTextModel(text, contentVersion))
                .subscribe(content -> {
                    ContentTextEvent event = new ContentTextEvent(content.id, content);
                    publisher.publishEvent(event);
                });
    }

    
    public void get(String id) {
        remote.content(id)
                .subscribe(content -> {
                    ContentUpdateEvent event =
                            new ContentUpdateEvent(content.id, content);
                    publisher.publishEvent(event);
                });
    }

    
    public void stars() {
        remote.contents(true)
                .subscribe(content -> {
                    ContentUpdateEvent event =
                            new ContentUpdateEvent(content.id, content);
                    publisher.publishEvent(event);
                });
    }

    
    public void clipboard() {
        remote.contents()
                .subscribe(content -> {
                    ContentUpdateEvent event =
                            new ContentUpdateEvent(content.id, content);
                    publisher.publishEvent(event);
                });
    }

    @Override
    public void onApplicationEvent(ClipboardEvent event) {
        switch (event.getType()) {
            case CLIPBOARD_CREATE:
                break;
            case CLIPBOARD_UPDATE:
                break;
            case CLIPBOARD_CHECK:
                break;
            case CLIPBOARD_REPORT:
                String content = (String)event.getPayloud().get("clipboard");
                create(content);
                break;
        }
    }

    @NotNull
    private ContentCreateModel createModel(@NotNull String text,
                                           String id, String account) {
        ContentCreateModel model = new ContentCreateModel();
        model.id = id;
        model.account = account;
        model.star = false;
        model.content = text;
        model.state = Content.ContentState.CONTENT_STATE_NORMAL.STATE;
        model.starVersion =
                model.contentVersion
                        = model.stateVersion
                        = model.create
                        = model.update = new Date();
        return model;
    }

    @NotNull
    private Content createContent(@NotNull String text, String id, String account) {
        Content model = new Content();
        model.id = id;
        model.account = account;
        model.star = false;
        model.content = text;
        model.state = Content.ContentState.CONTENT_STATE_NORMAL.STATE;
        model.starVersion =
                model.contentVersion
                        = model.stateVersion
                        = model.create
                        = model.update = new Date();
        model.status = Content.ContentStatus.CONTENT_STATUS_LOCAL.STATUS;
        model.update = new Date();
        model.create = new Date();
        return model;
    }

    @NotNull
    private ContentStarModel createContentStarModel(boolean star, @NotNull Date timestamp) {
        ContentStarModel contentStarModel = new ContentStarModel();
        contentStarModel.star = star;
        contentStarModel.starVersion = timestamp;
        return contentStarModel;
    }

    @NotNull
    private ContentStateModel createContentStateModel(@NotNull Content.ContentState state,
                                                      @NotNull Date timestamp) {
        ContentStateModel model = new ContentStateModel();
        model.state = state.STATE;
        model.stateVersion = timestamp;
        return model;
    }

    @NotNull
    private ContentTextModel createContentTextModel(@NotNull String text,
                                                    @NotNull Date timestamp) {
        ContentTextModel model = new ContentTextModel();
        model.content = text;
        model.contentVersion = timestamp;
        return model;
    }

    @NonNull
    private byte[] hash(@NonNull String str) {
        return digest.digest(str.getBytes(StandardCharsets.UTF_8));
    }
}
