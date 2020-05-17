package com.example.clipboard.client.service;

import com.example.clipboard.client.entity.Account;
import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.ClipboardEvent;
import com.example.clipboard.client.event.clipboard.ClipboardClearEvent;
import com.example.clipboard.client.event.clipboard.ClipboardUpdateEvent;
import com.example.clipboard.client.event.content.*;
import com.example.clipboard.client.event.ContentEvent;
import com.example.clipboard.client.helper.PojoCopyHelper;
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
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import oshi.SystemInfo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.*;

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

    /**
     * save content to local content cache
     * if id supplied will update
     * if id not supplied will insert
     * @param content content to be saved
     * @return saved content
     */
    public Content save(@NonNull Content content) {
        ensureRepositoryNotNull();

        // no id is supplied indicate that this content is new
        if(StringUtils.isEmpty(content.id)) {

            // content id is empty and content is empty
            if(StringUtils.isEmpty(content.content)) {
                throw new RuntimeException("Content cannot be empty: " + content);
            }

            content.id = UUID.randomUUID().toString();
            content.hash = hash(content.content);
            Optional<Content> optionalContent = repository.
                    findContentByContentEqualsAndHashEquals(content.content, content.hash);

            if(optionalContent.isEmpty()) {
                content.setDefaultIfAbsent();
                content.state = Content.ContentState.CONTENT_STATE_NORMAL.STATE;
                content.previous = content.state;
                content.status = Content.ContentStatus.CONTENT_STATUS_LOCAL.STATUS;
                SystemInfo systemInfo = new SystemInfo();
                content.device = systemInfo.getOperatingSystem().getVersionInfo().getVersion();
                content.setDefaultIfAbsent();
                Content saved = repository.save(content);
                ContentCreateEvent event = new ContentCreateEvent(this, saved, saved);
                publisher.publishEvent(event);
                return saved;
            }

            content = optionalContent.get();
            Content old = new Content();
            BeanUtils.copyProperties(content, old);
            content.timestamp = new Date();
            content.update = new Date();

            // what is same text is archived recycled or star
            switch (Content.ContentState.get(content.state)) {
                case CONTENT_STATE_NORMAL:
                case CONTENT_STATE_STAR:
                    break;
                case CONTENT_STATE_RECYCLE:
                case CONTENT_STATE_ARCHIVE:
                case CONTENT_STATE_DELETE:
                    content.state = content.previous;
                    if(content.state == null) {
                        content.state = Content.ContentState.CONTENT_STATE_NORMAL.STATE;
                    }
                    break;
            }
            content = repository.save(content);
            ContentUpdateEvent event = new ContentUpdateEvent(this, old, content);
            publisher.publishEvent(event);
            return content;
        }

        // an id is supplied indicate content changed or content from cloud sync
        Optional<Content> contentOptional = repository.findById(content.id);
        if(contentOptional.isEmpty()) { // a new content supplied with id
            // content may be the result of cloud synchronization
            if(StringUtils.isEmpty(content.content)) {
                throw new RuntimeException("Content is empty: " + content);
            }
            if(StringUtils.isEmpty(content.hash)) {
                content.hash = hash(content.content);
            }

            // check state
            if(content.state == null) {
                content.state = Content.ContentState.CONTENT_STATE_NORMAL.STATE;
            }
            if(content.previous == null) {
                content.previous = content.state;
            }

            content.timestamp = new Date();
            Content saved = repository.save(content);
            ContentCreateEvent contentCreateEvent = new ContentCreateEvent(this, saved, saved);
            publisher.publishEvent(contentCreateEvent);
            return content;
        }

        // content exists in local cache
        Content old = contentOptional.get();
        Content destination = new Content();
        BeanUtils.copyProperties(old, destination);

        boolean contentChangeFlag = false;
        boolean stateChangeFlag = false;

        if(content.content == null
                || content.content.equals(old.content)) {
            // no change from content
            contentChangeFlag = true;
        }

        if(content.state == null || content.state.equals(old.state)) {
            // no change from state
            stateChangeFlag = true;
        }

        PojoCopyHelper.merge(content, destination);
        destination.update = new Date();

        if(contentChangeFlag) {
            destination.timestamp = new Date();
        }

        if(stateChangeFlag) {
            destination.timestamp = new Date();
            destination.previous = old.state;
        }

        if(destination.state == null) {
            destination.state = Content.ContentState.CONTENT_STATE_NORMAL.STATE;
        }
        if(destination.previous == null) {
            destination.previous = destination.state;
        }

        destination = repository.save(destination);
        if(contentChangeFlag) {
            ContentUpdateEvent contentUpdateEvent = new ContentUpdateEvent(this, old, destination);
            publisher.publishEvent(contentUpdateEvent);
        } else if(stateChangeFlag) {
            ContentEvent event = null;
            switch (Content.ContentState.get(destination.state)) {
                case CONTENT_STATE_NORMAL:
                    event = new ContentNormalEvent(this, old, destination);
                    break;
                case CONTENT_STATE_ARCHIVE:
                    event = new ContentArchiveEvent(this, old, destination);
                    break;
                case CONTENT_STATE_RECYCLE:
                    event = new ContentRecycleEvent(this, old, destination);
                    break;
                case CONTENT_STATE_STAR:
                    event = new ContentUpdateEvent(this, old, destination);
                    break;
                case CONTENT_STATE_DELETE:
                    event = new ContentDeleteEvent(this, old, destination);
                    break;
                default:
            }
            publisher.publishEvent(event);
        }
        return destination;
    }

    public Content state(String id, Content.ContentState state) {
        ensureRepositoryNotNull();
        Optional<Content> contentOptional = repository.findById(id);
        if(contentOptional.isEmpty()) {
            throw new RuntimeException("Content not found by id: " + id);
        }
        Content content = contentOptional.get();
        Content old = new Content();
        BeanUtils.copyProperties(content, old);
        if(isStateChangeValid(Content.ContentState.get(content.state), state)) {

            content.update = new Date();
            content.previous = old.state;
            content.state = state.STATE;
            Content saved = repository.save(content);
            ContentEvent event = null;
            switch (state) {
                case CONTENT_STATE_NORMAL:
                    event = new ContentNormalEvent(this, old, saved);
                    break;
                case CONTENT_STATE_STAR:
                    event = new ContentStarEvent(this, old, saved);
                    break;
                case CONTENT_STATE_ARCHIVE:
                    event = new ContentArchiveEvent(this, old, saved);
                    break;
                case CONTENT_STATE_RECYCLE:
                    event = new ContentRecycleEvent(this, old, saved);
                    break;
                case CONTENT_STATE_DELETE:
                    event = new ContentDeleteEvent(this, old, saved);
                    break;
            }
            publisher.publishEvent(event);
            return saved;
        } else {
            throw new RuntimeException("Content State Invalid: " + content.state + " -> " + state);
        }
    }

    public Content text(String id, String text) {
        ensureRepositoryNotNull();
        if(StringUtils.isEmpty(text)) {
            throw new RuntimeException("Content cannot be empty!");
        }
        Optional<Content> contentOptional = repository.findById(id);
        if(contentOptional.isEmpty()) {
            throw new RuntimeException("Content not found by id: " + id);
        }
        Content content = contentOptional.get();
        Content old = new Content();
        BeanUtils.copyProperties(content, old);
        content.content = text;
        content.update = new Date();
        content.timestamp = new Date();
        /*
        todo prevent conflicts in modified content
         */
        content.hash = hash(content.content + new Date());
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
     * @param event publish clipboard event
     */
    public void clear(boolean event) {
        ensureRepositoryNotNull();
        repository.deleteAllInBatch();
        if(event) {
            ClipboardClearEvent clipboardClearEvent = new ClipboardClearEvent(this);
            publisher.publishEvent(clipboardClearEvent);
        }
    }

    /**
     * delete content by id from local cache
     * @param content content
     * @param event publish event or not
     * @param exception throw exception when not found
     */
    public void delete(@NonNull Content content, boolean event, boolean exception) {
        ensureRepositoryNotNull();
        Optional<Content> contentOptional = repository.findById(content.id);
        if(exception && contentOptional.isEmpty()) {
            throw new RuntimeException("Cannot find Content By Id: " + content);
        }
        repository.delete(content);
        if(event) {
            Content data = contentOptional.orElse(null);
            ContentEvent contentEvent = new ContentDeleteEvent(this,
                    data, data);
            publisher.publishEvent(contentEvent);
        }
    }



    /**
     * get a content mach all
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
        return repository.getContentsByStateIn(Arrays.asList(Content.ContentState.CONTENT_STATE_NORMAL.STATE,
                Content.ContentState.CONTENT_STATE_STAR.STATE));
    }

    public List<Content> getStar() {
        ensureRepositoryNotNull();
        return repository.getContentsByStateEquals(Content.ContentState.CONTENT_STATE_STAR.STATE);
    }

    public List<Content> getArchive() {
        ensureRepositoryNotNull();
        return repository.getContentsByStateEquals(Content.ContentState.CONTENT_STATE_ARCHIVE.STATE);
    }

    public List<Content> getRecycle() {
        ensureRepositoryNotNull();
        return repository.getContentsByStateEquals(Content.ContentState.CONTENT_STATE_RECYCLE.STATE);
    }


    public List<Content> gets(Content content) {
        ensureRepositoryNotNull();
        return repository.findAll(Example.of(content, ExampleMatcher.matchingAll()), Sort.by(Sort.Order.desc("update")));
    }

    /**
     * THIS IS WHERE CONTENT FROM CLIPBOARD IS RECORD
     * IF CONTENT IN CLIPBOARD EXISTS IN LOCAL DATABASE
     * UPDATE IT'S TIMESTAMP
     * OTHERWISE INSERT INTO LOCAL DATABASE
     * @param clipboardUpdateEvent event
     */
    @Override
    public void onApplicationEvent(ClipboardUpdateEvent clipboardUpdateEvent) {
        Content content = new Content();
        content.content = clipboardUpdateEvent.getContent();
        save(content);
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
        if(applicationInfo == null) {
            applicationInfo = context.getBean(ApplicationInfo.class);
        }
    }

    private boolean isStateChangeValid(@NonNull Content.ContentState before,
                                       @NonNull Content.ContentState now) {
        switch (before) {
            case CONTENT_STATE_NORMAL:
                switch (now) {
                    case CONTENT_STATE_ARCHIVE:
                    case CONTENT_STATE_STAR:
                    case CONTENT_STATE_RECYCLE:
                    case CONTENT_STATE_DELETE:
                        return true;
                    case CONTENT_STATE_NORMAL:
                    default:
                        return false;
                }
            case CONTENT_STATE_STAR:
                switch (now) {
                    case CONTENT_STATE_NORMAL:
                    case CONTENT_STATE_ARCHIVE:
                    case CONTENT_STATE_RECYCLE:
                    case CONTENT_STATE_DELETE:
                        return true;
                    case CONTENT_STATE_STAR:
                    default:
                        return false;
                }
            case CONTENT_STATE_ARCHIVE:
                switch (now) {
                    case CONTENT_STATE_NORMAL:
                    case CONTENT_STATE_STAR:
                    case CONTENT_STATE_RECYCLE:
                    case CONTENT_STATE_DELETE:
                        return true;
                    case CONTENT_STATE_ARCHIVE:
                    default:
                        return false;
                }
            case CONTENT_STATE_RECYCLE:
                switch (now) {
                    case CONTENT_STATE_NORMAL:
                    case CONTENT_STATE_STAR:
                    case CONTENT_STATE_ARCHIVE:
                    case CONTENT_STATE_DELETE:
                        return true;
                    case CONTENT_STATE_RECYCLE:
                    default:
                        return false;
                }
            case CONTENT_STATE_DELETE:
            default:
                return false;
        }
    }
    @NonNull
    private String hash(@NonNull String str) {
        ensureDigestNotNull();
        byte[] bytes = digest.digest(str.getBytes(StandardCharsets.UTF_8));
        return Base64Utils.encodeToString(bytes);
    }

}
