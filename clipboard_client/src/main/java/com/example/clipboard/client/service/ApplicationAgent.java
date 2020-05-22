package com.example.clipboard.client.service;

import com.example.clipboard.client.repository.AppRepository;
import com.example.clipboard.client.repository.entity.App;
import com.example.clipboard.client.service.worker.event.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Lazy(false)
@Component
public class ApplicationAgent implements ApplicationListener<AppEvent> {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private AppContext appContext;

    @Override
    public void onApplicationEvent(AppEvent event) {
        if(event instanceof AppStartEvent) {
            // init
            init();
        } else if(event instanceof AccountLoginEvent) {
            appContext.auto = true;
            App app = new App();
            app.id = appContext.id;
            app.username = appContext.username;
            app.email = appContext.email;
            app.account = appContext.account;
            app.token = appContext.token;
            context.getBean(AppRepository.class)
                    .save(app);
            user();
        } else if(event instanceof AccountLogoutEvent) {
            context.publishEvent(new AgentEvent(this, AgentEvent.AgentEventType.STOP_CLOUD_AGENT));
            appContext.auto = false;
        }
    }

    private App load() {
        return context.getBean(AppRepository.class).findById("app").orElseGet(App::new);
    }

    private void init() {

        App app = load();
        appContext.id = app.id;
        appContext.account = app.account;
        appContext.username = app.username;
        appContext.email = app.email;
        appContext.token = app.token;
        appContext.baseUrl = "http://localhost:8080";
        appContext.period = 1000L;
        appContext.limit = 100;
        if(StringUtils.isEmpty(app.account)) {
            visitor();
        } else {
            appContext.auto = true;
            user();
        }

    }

    private void visitor() {
        context.publishEvent(new AgentEvent(this, AgentEvent.AgentEventType.START_LOCAL_AGENT));

    }

    private void user() {
        context.publishEvent(new AgentEvent(this, AgentEvent.AgentEventType.START_ALL));

    }

}
