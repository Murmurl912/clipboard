package com.example.clipboard.client.lifecycle;

import com.example.clipboard.client.entity.AccessToken;
import com.example.clipboard.client.entity.Account;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ApplicationInfo {

    public volatile AtomicBoolean isLogin = new AtomicBoolean(false);

    public volatile AtomicReference<Account> account = new AtomicReference<>(null);

    public volatile AtomicReference<AccessToken> accessToken = new AtomicReference<>(null);

}
