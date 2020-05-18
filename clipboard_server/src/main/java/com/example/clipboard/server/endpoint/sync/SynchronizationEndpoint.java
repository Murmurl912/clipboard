package com.example.clipboard.server.endpoint.sync;

import com.example.clipboard.server.service.event.ContentEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@RestController
public class SynchronizationEndpoint implements ApplicationListener<ContentEvent>,
        Consumer<FluxSink<ContentEvent>> {

    private FluxSink<ContentEvent> eventFluxSink;

    @GetMapping(value = "/sync/account/{account}/event/content",
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Flux<ContentEvent> listen( @PathVariable String account) {
        return Flux.create(this)
                .filter(contentEvent -> contentEvent.getAccount().equals(account))
                .map(contentEvent -> {
                    System.out.println(contentEvent);
                    return contentEvent;
                })
                .share();
    }

    @PostMapping(value = "/sync/account/{account}/event/content", consumes = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Flux<ContentEvent> send(@PathVariable String account, @RequestBody Flux<ContentEvent> eventFlux) {
        return eventFlux.map(contentEvent -> {
            System.out.println(contentEvent);
            return contentEvent;
        });
    }

    @Override
    public void onApplicationEvent(ContentEvent contentEvent) {
        if(eventFluxSink != null) {
            eventFluxSink.next(contentEvent);
        }
    }

    @Override
    public void accept(FluxSink<ContentEvent> contentEventFluxSink) {
        eventFluxSink = contentEventFluxSink;
    }
}
