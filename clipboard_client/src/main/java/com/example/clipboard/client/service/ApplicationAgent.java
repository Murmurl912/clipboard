package com.example.clipboard.client.service;

import com.example.clipboard.client.repository.AppRepository;
import com.example.clipboard.client.repository.entity.App;
import com.example.clipboard.client.service.worker.event.AgentEvent;
import com.example.clipboard.client.service.worker.event.AppEvent;
import com.example.clipboard.client.service.worker.event.AppStartEvent;
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
        }
    }

    private App load() {
        return context.getBean(AppRepository.class).findById("app").orElseGet(App::new);
    }

    private void init() {

        App app = load();
        appContext.id = app.id;
        appContext.account = app.account;
        appContext.avatar = app.avatar;
        appContext.username = app.username;
        appContext.email = app.email;
        appContext.token = app.token;

        if(StringUtils.isEmpty(app.account)) {
            visitor();
        } else {
            user();
        }

    }

    private void visitor() {
        context.publishEvent(new AgentEvent(this, AgentEvent.AgentEventType.START_LOCAL_AGENT));

    }

    private void user() {
        context.publishEvent(new AgentEvent(this, AgentEvent.AgentEventType.START_LOCAL_AGENT));

    }

}
