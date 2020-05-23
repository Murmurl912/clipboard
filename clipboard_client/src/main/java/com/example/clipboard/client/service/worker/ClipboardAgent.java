package com.example.clipboard.client.service.worker;

import com.example.clipboard.client.service.AppContext;
import com.example.clipboard.client.service.worker.event.AgentEvent;
import com.example.clipboard.client.service.worker.event.AgentStatusChangeEvent;
import com.example.clipboard.client.service.worker.event.ClipboardEvent;
import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Lazy(false)
@Service
public class ClipboardAgent implements ApplicationListener<AgentEvent> {

    private final ApplicationEventPublisher publisher;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TaskScheduler scheduler;
    private final AppContext context;
    private final AtomicBoolean connectionAlive = new AtomicBoolean(false);
    private ScheduledFuture<?> future;
    private Disposable disposable;
    private String content;

    public ClipboardAgent(ApplicationEventPublisher publisher,
                          TaskScheduler scheduler, AppContext context) {
        this.publisher = publisher;
        this.scheduler = scheduler;
        this.context = context;
    }

    private void local() {
        Platform.runLater(() -> {
            Clipboard clipboard =
                    Clipboard.getSystemClipboard();
            if (clipboard.hasString()) {
                String now = clipboard.getString();

                if (Objects.equals(content, now)) {
                    return;
                }
                content = now;
                HashMap<String, Object> payload = new HashMap<>();
                payload.put("clipboard", now);
                payload.put("timestamp", new Date());
                publisher.publishEvent(new ClipboardEvent(ClipboardEvent.EventSource.LOCAL_SOURCE,
                        ClipboardEvent.EventType.CLIPBOARD_REPORT, payload));
            }
        });
    }

    private Disposable cloud() {
        return disposable =
                WebClient.create(context.baseUrl)
                        .get()
                        .uri(context.eventUrl, context.account)
                        .accept(MediaType.APPLICATION_STREAM_JSON)
                        .retrieve()
                        .onStatus(HttpStatus::isError, new Function<ClientResponse, Mono<? extends Throwable>>() {
                            @Override
                            public Mono<? extends Throwable> apply(ClientResponse response) {
                                logger.info("Cloud Connection Error: " + response.statusCode());
                                connectionAlive.set(false);
                                publisher.publishEvent(new AgentStatusChangeEvent(AgentStatusChangeEvent.EventType.CONNECTION_LOST));
                                return null;
                            }
                        })
                        .bodyToFlux(ClipboardEventModel.class)
                        .map(model -> {
                            connectionAlive.set(true);
                            logger.info("Receive Cloud Clipboard Event: " + model.toString());
                            return model;
                        })
                        .doOnError(e -> {
                            logger.info("Cloud Connection Error: " + e.toString());
                            connectionAlive.set(false);
                            publisher.publishEvent(new AgentStatusChangeEvent(AgentStatusChangeEvent.EventType.CONNECTION_LOST));
                        })
                        .subscribe(model -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("event", model);
                            publisher.publishEvent(new ClipboardEvent(ClipboardEvent.EventSource.CLOUD_SOURCE,
                                    ClipboardEvent.EventType.CLIPBOARD_SYNC_EVENT, map));
                        });
    }

    @Override
    public void onApplicationEvent(AgentEvent event) {
        logger.info("Received StarAgentEvent");

        switch (event.getType()) {
            case START_ALL:
                startCloud();
                startLocal();
                break;
            case START_CLOUD_AGENT:
                startCloud();
                break;
            case START_LOCAL_AGENT:
                startLocal();
                break;
            case STOP_ALL:
                stopCloud();
                stopLocal();
                break;
            case STOP_CLOUD_AGENT:
                stopCloud();
                break;
            case STOP_LOCAL_AGENT:
                stopLocal();
                break;
        }
    }

    private void startCloud() {
        if (disposable == null) {
            logger.info("Start Cloud Clipboard Agent");
            disposable = cloud();
            return;
        }

        if (disposable.isDisposed()) {
            disposable = cloud();
            return;
        }

        if (connectionAlive.get()) {
            return;
        }

        disposable = cloud();
    }

    private void stopCloud() {
        if (disposable == null || disposable.isDisposed()) {
            return;
        }

        disposable.dispose();
        connectionAlive.set(false);
    }

    private void startLocal() {
        if (future == null || future.isCancelled()) {
            logger.info("Star Local Clipboard Agent");
            future = scheduler.scheduleAtFixedRate(this::local, context.period);
        } else {
            logger.info("Local Clipboard Agent Is Running, Ignore Start Agent Event");
        }
    }

    private void stopLocal() {
        if (future == null || future.isCancelled()) {
            return;
        }

        future.cancel(true);
    }

}
