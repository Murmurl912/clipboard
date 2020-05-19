package com.example.clipboard.client.service;

import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.repository.CachedContentRepository;
import com.example.clipboard.client.repository.RemoteContentRepository;
import com.example.clipboard.client.repository.model.ContentCreateModel;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClipboardService implements ClipboardServiceInterface {

    private RemoteContentRepository remote;
    private CachedContentRepository cached;

    @Override
    public Content create(String text) {
        if(StringUtils.isEmpty(text)) {
            throw new RuntimeException();
        }
        ContentCreateModel model = new ContentCreateModel();
        model.account = "murmur";
        model.id = UUID.randomUUID().toString();
        model.star = false;
        model.content = text;
        model.state = Content.ContentState.CONTENT_STATE_NORMAL.STATE;
        model.starVersion =
                model.contentVersion
                        = model.stateVersion
                        = model.create
                        = model.update = new Date();
        remote.create(model);
        return null;
    }

    @Override
    public Content star(String id,
                        boolean star) {
        return null;
    }

    @Override
    public Content state(String id,
                         Content.ContentState state) {
        return null;
    }

    @Override
    public Content text(String id,
                        String text) {
        return null;
    }

    @Override
    public Optional<Content> get(String id) {
        return Optional.empty();
    }

    @Override
    public List<Content> stars() {
        return null;
    }

    @Override
    public List<Content> clipboard() {
        return null;
    }

    @NotNull
    private ContentCreateModel createModel(@NotNull String text) {
        ContentCreateModel model = new ContentCreateModel();
        model.account = "murmur";
        model.id = UUID.randomUUID().toString();
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
    private Content createContent(@NotNull String text) {
        Content model = new Content();
        model.account = "murmur";
        model.id = UUID.randomUUID().toString();
        model.star = false;
        model.content = text;
        model.state = Content.ContentState.CONTENT_STATE_NORMAL.STATE;
        model.starVersion =
                model.contentVersion
                        = model.stateVersion
                        = model.create
                        = model.update = new Date();
        model.status = Content.ContentStatus.CONTENT_STATUS_LOCAL.STATUS;
        return model;
    }


    private void createFromContent() {
        ContentCreateModel contentCreateModel = new ContentCreateModel();

    }
}
