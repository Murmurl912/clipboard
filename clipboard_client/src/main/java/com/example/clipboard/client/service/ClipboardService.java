package com.example.clipboard.client.service;

import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.content.ContentCreateEvent;
import com.example.clipboard.client.event.content.ContentStarEvent;
import com.example.clipboard.client.repository.CachedContentRepository;
import com.example.clipboard.client.repository.RemoteContentRepository;
import com.example.clipboard.client.repository.model.ContentCreateModel;
import com.example.clipboard.client.repository.model.ContentStarModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClipboardService {

    private final RemoteContentRepository remote;
    private final CachedContentRepository cached;
    private final ApplicationEventPublisher publisher;
    private final MessageDigest digest;
    private final String account = "murmur";
    
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
        ContentCreateModel model = createModel(text, account, uuid);
        remote.create(model)
                .subscribe(data -> {
                    ContentCreateEvent event =
                            new ContentCreateEvent(this, data, data);
                    publisher.publishEvent(event);
                });
    }

    
    public void star(String id,
                        boolean star,
                        Date starVersion) {
        remote.star(id, createContentStarModel(star, starVersion))
                .subscribe(content -> {

                });
    }

    
    public Content state(String id,
                         Content.ContentState state) {
        return null;
    }

    
    public Content text(String id,
                        String text) {
        return null;
    }

    
    public Optional<Content> get(String id) {
        return Optional.empty();
    }

    
    public List<Content> stars() {
        return null;
    }

    
    public List<Content> clipboard() {
        return null;
    }

    @NotNull
    private ContentCreateModel createModel(@NotNull String text, String id, String account) {
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


    private ContentStarModel createContentStarModel(boolean star, Date timestamp) {
        ContentStarModel contentStarModel = new ContentStarModel();
        contentStarModel.star = star;
        contentStarModel.starVersion = timestamp;
        return contentStarModel;
    }

    @NonNull
    private byte[] hash(@NonNull String str) {
        return digest.digest(str.getBytes(StandardCharsets.UTF_8));
    }
}
