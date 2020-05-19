package com.example.clipboard.client.service;

import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.ContentEvent;
import com.example.clipboard.client.event.clipboard.ClipboardClearEvent;
import com.example.clipboard.client.event.clipboard.ClipboardUpdateEvent;
import com.example.clipboard.client.event.content.ContentCreateEvent;
import com.example.clipboard.client.event.content.ContentDeleteEvent;
import com.example.clipboard.client.event.content.ContentStarEvent;
import com.example.clipboard.client.event.content.ContentUpdateEvent;
import com.example.clipboard.client.lifecycle.ApplicationInfo;
import com.example.clipboard.client.repository.CachedContentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//todo implement pageable

/**
 * this class represent a local cached clipboard
 * ui components etc. should use this class as datasource
 * remote clipboard should put data in local cached clipboard
 */
@Lazy(false)
@Service
public class CachedClipboardService implements ApplicationListener<ClipboardUpdateEvent> {

    private final ApplicationContext context;
    private final ApplicationEventPublisher publisher;
    private CachedContentRepository repository;
    private MessageDigest digest;
    private ApplicationInfo applicationInfo;

    public CachedClipboardService(ApplicationContext context,
                                  ApplicationEventPublisher publisher) {
        this.context = context;
        this.publisher = publisher;
    }

    public Content create(String text) {
        if (StringUtils.isEmpty(text)) {
            throw new RuntimeException("Content cannot be empty");
        }
        byte[] hash = hash(text);
        Optional<Content> contentOptional = repository.findContentByHashEquals(hash);
        if (contentOptional.isEmpty()) {
            Content content = new Content();
            content.id = UUID.randomUUID().toString();
            content.account = "local";
            content.content = text;
            content.contentVersion = new Date(System.currentTimeMillis());
            content.state = Content.ContentState.CONTENT_STATE_NORMAL.STATE;
            content.stateVersion = new Date(System.currentTimeMillis());
            content.star = false;
            content.starVersion = new Date(System.currentTimeMillis());
            content.hash = hash;
            content.status = Content.ContentStatus.CONTENT_STATUS_LOCAL.STATUS;
            content.create = new Date(System.currentTimeMillis());
            content.update = new Date(System.currentTimeMillis());
            content = repository.save(content);
            ContentCreateEvent contentCreateEvent = new ContentCreateEvent(this, content, content);
            publisher.publishEvent(contentCreateEvent);
            return content;
        } else {
            Content original = contentOptional.get();
            original.starVersion = new Date(System.currentTimeMillis());
            original.update = new Date(System.currentTimeMillis());
            original.contentVersion = new Date(System.currentTimeMillis());
            original.stateVersion = new Date(System.currentTimeMillis());
            ContentCreateEvent contentCreateEvent = new ContentCreateEvent(this, original, original);
            publisher.publishEvent(contentCreateEvent);
            return original;
        }
    }

    protected Content cache(Content content) {
        return repository.save(content);
    }

    public Content star(String id, boolean star) {
        ensureDigestNotNull();
        Optional<Content> contentOptional = repository.findById(id);
        if (contentOptional.isEmpty()) {
            throw new RuntimeException("Content not found by id: " + id);
        }
        Content content = contentOptional.get();
        Content old = new Content();
        BeanUtils.copyProperties(content, old);
        content.update = new Date(System.currentTimeMillis());
        content.star = star;
        content = repository.save(content);
        ContentStarEvent starEvent = new ContentStarEvent(this, old, content);
        publisher.publishEvent(starEvent);
        return content;
    }

    public Content state(String id, Content.ContentState state) {
        ensureRepositoryNotNull();
        Optional<Content> contentOptional = repository.findById(id);
        if (contentOptional.isEmpty()) {
            throw new RuntimeException("Content not found by id: " + id);
        }
        Content content = contentOptional.get();
        Content old = new Content();
        BeanUtils.copyProperties(content, old);

        if (old.state == Content.ContentState.CONTENT_STATE_DELETE.STATE) {
            throw new RuntimeException("Content is deleted: " + content);
        }

        if (old.state == state.STATE) {
            return old;
        } else {
            content.state = state.STATE;
            content = repository.save(content);
            ContentDeleteEvent event = new ContentDeleteEvent(this, old, content);
            publisher.publishEvent(event);
            return content;
        }
    }

    public Content text(String id, String text) {
        ensureRepositoryNotNull();
        if (StringUtils.isEmpty(text)) {
            throw new RuntimeException("Content cannot be empty!");
        }
        Optional<Content> contentOptional = repository.findById(id);
        if (contentOptional.isEmpty()) {
            throw new RuntimeException("Content not found by id: " + id);
        }
        Content content = contentOptional.get();
        Content old = new Content();
        BeanUtils.copyProperties(content, old);
        content.content = text;
        content.update = new Date(System.currentTimeMillis());
        // todo prevent conflicts in modified content
        content.hash = hash(content.content);
        content = repository.save(content);
        ContentUpdateEvent event = new ContentUpdateEvent(this, old, content);
        publisher.publishEvent(event);
        return null;
    }

    /**
     * this operation will clear local cached clipboard
     * cloud side will not be affected
     * it should only be call when user logout
     * or specified by user
     *
     * @param event publish clipboard event
     */
    public void clear(boolean event) {
        ensureRepositoryNotNull();
        repository.deleteAllInBatch();
        if (event) {
            ClipboardClearEvent clipboardClearEvent = new ClipboardClearEvent(this);
            publisher.publishEvent(clipboardClearEvent);
        }
    }

    /**
     * delete content by id from local cache
     *
     * @param content   content
     * @param event     publish event or not
     * @param exception throw exception when not found
     */
    public void delete(@NonNull Content content, boolean event, boolean exception) {
        // todo rewrite
        ensureRepositoryNotNull();
        Optional<Content> contentOptional = repository.findById(content.id);
        if (exception && contentOptional.isEmpty()) {
            throw new RuntimeException("Cannot find Content By Id: " + content);
        }
        repository.delete(content);
        if (event) {
            Content data = contentOptional.orElse(null);
            ContentEvent contentEvent = new ContentDeleteEvent(this,
                    data, data);
            publisher.publishEvent(contentEvent);
        }
    }


    /**
     * get a content mach all
     *
     * @param content match data
     * @return one result
     */
    public Optional<Content> get(Content content) {
        ensureRepositoryNotNull();
        return repository.findOne(Example.of(content, ExampleMatcher.matchingAll()));
    }

    public List<Content> gets() {
        ensureRepositoryNotNull();
        return repository.findAll(Sort.by(Sort.Order.desc("update")));
    }

    public List<Content> getClipboard() {
        ensureRepositoryNotNull();
        return repository.getContentsByStateEquals(Content.ContentState.CONTENT_STATE_NORMAL.STATE);
    }

    public List<Content> getStar() {
        ensureRepositoryNotNull();
        return repository.getContentsByStateEqualsAndStarEquals(Content.ContentState.CONTENT_STATE_NORMAL.STATE, true);
    }


    public List<Content> gets(Content content) {
        ensureRepositoryNotNull();
        return repository.findAll(Example.of(content, ExampleMatcher.matchingAll()), Sort.by(Sort.Order.desc("update")));
    }

    /**
     * THIS IS WHERE CONTENT FROM CLIPBOARD IS RECORD
     * IF CONTENT IN CLIPBOARD EXISTS IN LOCAL DATABASE
     * UPDATE IT'S Date
     * OTHERWISE INSERT INTO LOCAL DATABASE
     *
     * @param clipboardUpdateEvent event
     */
    @Override
    public void onApplicationEvent(ClipboardUpdateEvent clipboardUpdateEvent) {
        Content content = create(clipboardUpdateEvent.getContent());
    }

    private void ensureRepositoryNotNull() {
        if (repository == null) {
            repository = context.getBean(CachedContentRepository.class);
        }
    }

    private void ensureDigestNotNull() {
        if (digest == null) {
            digest = context.getBean(MessageDigest.class);
        }
    }

    private void ensureApplicationInfoNotNull() {
        if (applicationInfo == null) {
            applicationInfo = context.getBean(ApplicationInfo.class);
        }
    }

    @NonNull
    private byte[] hash(@NonNull String str) {
        ensureDigestNotNull();
        return digest.digest(str.getBytes(StandardCharsets.UTF_8));
    }

}
