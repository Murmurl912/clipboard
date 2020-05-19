package com.clipboard.clipboard_store.service;

import com.clipboard.clipboard_store.event.ClipboardEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

//@Scope("prototype")
@Service
public class ClipboardEventPublisher implements
        Consumer<FluxSink<ClipboardEvent>>,
        ApplicationListener<ClipboardEvent> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private FluxSink<ClipboardEvent> sink = null;

    @Override
    public void onApplicationEvent(ClipboardEvent clipboardEvent) {
        if(sink == null) {
            logger.error("Flux sink is null: " +  clipboardEvent);
            return;
        }

        sink.next(clipboardEvent);
    }

    @Override
    public void accept(FluxSink<ClipboardEvent> clipboardEventFluxSink) {
        sink = clipboardEventFluxSink;
    }

}
