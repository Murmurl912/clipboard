package com.example.clipboard.client.service;

import com.example.clipboard.client.entity.Account;
import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.event.ClipboardEvent;
import com.example.clipboard.client.event.ContentEvent;
import com.example.clipboard.client.lifecycle.ApplicationInfo;
import com.example.clipboard.client.repository.CachedContentRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import oshi.SystemInfo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Lazy(false)
@Service
public class CachedClipboardService implements ApplicationListener<ClipboardEvent> {

    private final ApplicationContext context;
    private final ApplicationEventPublisher publisher;
    private CachedContentRepository repository;
    private MessageDigest digest;

    public CachedClipboardService(ApplicationContext context, ApplicationEventPublisher publisher) {
        this.context = context;
        this.publisher = publisher;
    }

    public Content put(Content content) {
        if (repository == null) {
            repository = context.getBean(CachedContentRepository.class);
        }

        return repository.save(content);
    }

    public Content save(Content content) {
        if (repository == null) {
            repository = context.getBean(CachedContentRepository.class);
        }

        return repository.save(content);
    }

    public Optional<Content> get(Content content) {
        if (repository == null) {
            repository = context.getBean(CachedContentRepository.class);
        }
        return repository.findOne(Example.of(content, ExampleMatcher.matchingAll()));
    }

    public Optional<Content> equal(Content content) {
        return null;
    }

    public List<Content> gets() {
        if (repository == null) {
            repository = context.getBean(CachedContentRepository.class);
        }
        return repository.findAll(Sort.by(Sort.Order.desc("update")));
    }

    public List<Content> gets(Content content) {
        if (repository == null) {
            repository = context.getBean(CachedContentRepository.class);
        }
        return repository.findAll(Example.of(content, ExampleMatcher.matchingAll()), Sort.by(Sort.Order.desc("update")));
    }

    @Override
    public void onApplicationEvent(ClipboardEvent clipboardEvent) {
        if (repository == null) {
            repository = context.getBean(CachedContentRepository.class);
        }

        if (digest == null) {
            digest = context.getBean(MessageDigest.class);
        }
        String content = clipboardEvent.getContent();
        byte[] bytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
        String hash = Base64Utils.encodeToString(bytes);
        Content data = new Content();
        data.hash = hash;
        data.content = content;
        Optional<Content> contentOptional = repository.findOne(Example.of(data, ExampleMatcher.matchingAll()));
        ContentEvent event = null;
        if (contentOptional.isEmpty()) {
            Account account = context.getBean(ApplicationInfo.class).account.get();
            if (account == null) {
                data.account = "local";
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
            event = new ContentEvent(this, null, data,
                    ContentEvent.ContentEventType.CONTENT_EVENT_TYPE_CREATION);
        } else {
            data = contentOptional.get();
            data.update = new Date();
            event = new ContentEvent(this, contentOptional.get(), data,
                    ContentEvent.ContentEventType.CONTENT_EVENT_TYPE_UPDATE);
        }
        context.getBean(ApplicationInfo.class).cacheTimestamp.set(System.currentTimeMillis());
        publisher.publishEvent(event);
    }
}
