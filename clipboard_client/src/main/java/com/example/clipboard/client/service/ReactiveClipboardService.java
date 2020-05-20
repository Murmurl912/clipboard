package com.example.clipboard.client.service;

import com.example.clipboard.client.repository.entity.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class ReactiveClipboardService {

    public Mono<Content> create(String text) {

        return null;
    }

    public Mono<Content> check(String text) {
        return null;
    }

}
