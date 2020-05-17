package com.example.clipboard.client.service;

import com.example.clipboard.client.entity.Account;
import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.ClipboardUpdateEvent;
import com.example.clipboard.client.event.ContentEvent;
import com.example.clipboard.client.helper.PojoCopyHelper;
import com.example.clipboard.client.lifecycle.ApplicationInfo;
import com.example.clipboard.client.repository.CachedContentRepository;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// todo publish event only if content#content is changed or created

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

    public Content put(@NonNull Content content) {
        ensureRepositoryNotNull();
        if(StringUtils.isEmpty(content.content)) {
            throw new RuntimeException("Content cannot be empty: " + content);
        }
        content.hash = hash(content.content);
        content.setDefaultIfAbsent();
        // TODO PUBLISH EVENT AND UPDATE CACHE TIMESTAMP
        return repository.save(content);
    }

    public Content save(@NonNull Content content) {
        ensureRepositoryNotNull();
        Optional<Content> contentOptional = repository.findById(content.id);
        if(contentOptional.isEmpty()) {
            throw new RuntimeException("Cannot find Content By Id: " + content);
        }

        // TODO PUBLISH EVENT AND UPDATE CAHCE TIMESTAMP
        Content destination = contentOptional.get();
        PojoCopyHelper.merge(content, destination);
        return repository.save(destination);
    }

    public Optional<Content> get(Content content) {
        ensureRepositoryNotNull();
        return repository.findOne(Example.of(content, ExampleMatcher.matchingAll()));
    }

    public List<Content> gets() {
        ensureRepositoryNotNull();
        return repository.findAll(Sort.by(Sort.Order.desc("update")));
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
     * @param clipboardUpdateEvent clipboard event
     */
    @Override
    public void onApplicationEvent(ClipboardUpdateEvent clipboardUpdateEvent) {
        ensureDigestNotNull();
        ensureRepositoryNotNull();
        ensureApplicationInfoNotNull();

        Content data = new Content();
        data.content = clipboardUpdateEvent.getContent();
        data.hash = hash(data.content);;

        Optional<Content> contentOptional = repository.findOne(Example.of(data, ExampleMatcher.matchingAll()));
        ContentEvent event = null;

        if (contentOptional.isEmpty()) {
            Account account = applicationInfo.account.get();
            if (account == null) {
                data.account = "local"; // not account login
            } else {
                data.account = account.id;
            }

            data.id = UUID.randomUUID().toString();
            data.update = new Date();
            data.create = new Date();
            data.status = Content.ContentStatus.CONTENT_STATUS_LOCAL.STATUS;
            data.star = false;
            data.archive = false;
            data.recycle = false;
            SystemInfo systemInfo = new SystemInfo();
            data.device = systemInfo.getOperatingSystem().getVersionInfo().getVersion();
            repository.save(data);
            // todo publish event
        } else {
            data = contentOptional.get();
            data.update = new Date();

            // todo publish event
        }
        applicationInfo.cacheTimestamp.set(System.currentTimeMillis());
        publisher.publishEvent(event);
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

    @NonNull
    private String hash(@NonNull String str) {
        byte[] bytes = digest.digest(str.getBytes(StandardCharsets.UTF_8));
        return Base64Utils.encodeToString(bytes);
    }


}
