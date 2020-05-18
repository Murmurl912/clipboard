package com.example.clipboard.server;

import com.example.clipboard.server.service.event.ContentEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.sql.Timestamp;
import java.util.UUID;

@SpringBootTest
public class WebStreamTest {

    @Test
    public void doTest() {
        WebClient client = WebClient.create("http://localhost:8080/sync/account/mur/content");
        Flux<ContentEvent> eventFlux = Flux.create(sink -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    ContentEvent contentEvent = new ContentEvent(this,
                            "mur", ContentEvent.ContentEventType.CONTENT_EVENT_ARCHIVE.type,
                            new Timestamp(System.currentTimeMillis()), UUID.randomUUID().toString());
                    sink.next(contentEvent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        client.post().accept(MediaType.APPLICATION_STREAM_JSON)
                .body(eventFlux, eventFlux.getClass())
                .retrieve()
                .bodyToFlux(ContentEvent.class)
                .map(contentEvent -> {
                    System.out.println(contentEvent);
                    return contentEvent;
                })
                .subscribe();
    }

}
