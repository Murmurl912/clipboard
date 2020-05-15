package com.example.clipboard.client.service;

import com.example.clipboard.client.entity.AccessToken;
import com.example.clipboard.client.entity.Clipboard;
import com.example.clipboard.client.lifecycle.ApplicationInfo;
import com.example.clipboard.client.lifecycle.ApplicationStartEvent;
import com.example.clipboard.client.repository.LocalAccessTokenRepository;
import com.example.clipboard.client.repository.LocalAccountRepository;
import com.example.clipboard.client.repository.LocalClipboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Service
public class ApplicationStateService
        implements ApplicationListener<ApplicationStartEvent> {

    @Autowired
    private ApplicationInfo applicationInfo;
    @Autowired
    private LocalAccessTokenRepository accessTokenRepository;
    @Autowired
    private LocalAccountRepository accountRepository;
    @Autowired
    private LocalClipboardRepository clipboardRepository;
    @Autowired
    private TaskScheduler taskScheduler;
    @Override
    public void onApplicationEvent(ApplicationStartEvent applicationStartEvent) {
        taskScheduler.scheduleWithFixedDelay(this::init, Duration.ofMillis(20));
        init();
    }

    private void init() {
        checkAccount();
        checkLocal();
    }

    private void checkAccount() {
        Iterable<AccessToken> tokens = accessTokenRepository.findAll();
        AccessToken token = null;
        for(AccessToken accessToken : tokens) {
            if(accessToken.expire.after(new Date())) {
                token = accessToken;
                break;
            }
        }

        if(token == null) {
            applicationInfo.isLogin.set(false);
        }
    }

    private void checkLocal() {
        Optional<Clipboard> clipboardOptional = clipboardRepository.findById("local");
        if(clipboardOptional.isEmpty()) {
            Clipboard clipboard = new Clipboard();
            clipboard.name = "local";
            clipboard.checkpoint = new Date();
            clipboardRepository.save(clipboard);
        }
    }
}
