package com.example.clipboard.client.lifecycle;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ApplicationInfo {

    public volatile AtomicBoolean isLogin = new AtomicBoolean(false);

}
