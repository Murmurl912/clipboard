package com.example.clipboard.client.service;

import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.lifecycle.ApplicationInfo;
import com.example.clipboard.client.repository.LocalClipboardRepository;
import com.example.clipboard.client.repository.LocalContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ClipboardService {

    @Autowired
    private LocalClipboardRepository localClipboardRepository;
    @Autowired
    private LocalContentRepository localContentRepository;
    @Autowired
    private ApplicationInfo applicationInfo;

    public Content put(Content content) {
        if(!applicationInfo.isLogin.get()) {
            return putLocally(content);
        }
        return null;
    }

    private Content putLocally(Content content) {
        if(content == null) {
            return null;
        }

        content.account = null;
        content.id = UUID.randomUUID().toString();
        content.update = new Date();
        content.create = new Date();
        content.status = Content.ContentStatus.CONTENT_STATUS_LOCAL;
        return localContentRepository.save(content);
    }


    public Content save(Content content) {
        if(!applicationInfo.isLogin.get()) {
            return saveLocally(content);
        }
        return null;
    }

    private Content saveLocally(Content content) {
        if(content == null) {
            return null;
        }

        content.create = new Date();
        content.status = Content.ContentStatus.CONTENT_STATUS_LOCAL;
        return localContentRepository.save(content);
    }

    public void delete(String id) {
        if(!applicationInfo.isLogin.get()) {
            deleteLocally(id);
        }
    }

    private void deleteLocally(String id) {
        if(!applicationInfo.isLogin.get()) {
            localContentRepository.deleteById(id);
        }
    }

    public Iterable<Content> get() {
        if(!applicationInfo.isLogin.get()) {
            return getLocally();
        }
        return null;
    }

    public Iterable<Content> getLocally() {
        return localContentRepository.findAll();
    }

}
