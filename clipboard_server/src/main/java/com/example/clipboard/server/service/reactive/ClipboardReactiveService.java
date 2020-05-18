package com.example.clipboard.server.service.reactive;

import com.example.clipboard.server.entity.Content;
import com.example.clipboard.server.exception.ContentNotFoundException;
import com.example.clipboard.server.exception.UserNotFoundException;
import com.example.clipboard.server.helper.CopyHelper;
import com.example.clipboard.server.repository.AccountRepository;
import com.example.clipboard.server.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class ClipboardReactiveService {

    @Autowired
    private ContentRepository repository;
    @Autowired
    private AccountRepository accountRepository;


    public Mono<Content> create(Mono<Content> contentMono) {
        return contentMono.flatMap( content ->
                accountRepository.countAccountById(content.account)
                        .handle((count, sink) -> {
                            if(count < 1) {
                                sink.error(new UserNotFoundException());
                                return;
                            }
                            content.create = new Date();
                            content.update = new Date();
                            sink.next(content);
                        }).cast(Content.class)
        ).flatMap(repository::insert);
    }

    public Mono<Void> delete(Mono<String> id) {
        return repository.deleteById(id);
    }

    public Mono<Content> update(Mono<Content> contentMono) {
        return contentMono.flatMap(content ->
                repository.findById(content.id)
                        .switchIfEmpty(Mono.error(ContentNotFoundException::new))
                        .flatMap(source -> {
                            try {
                                content.update = null;
                                content.create = null;
                                content.account = null;
                                CopyHelper.merge(source, content);
                                source.update = new Date();
                                return repository.save(source);
                            } catch (IllegalAccessException e) {
                                return Mono.error(e);
                            }

                        })
        );
    }

    public Mono<Content> get(Mono<String> id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(ContentNotFoundException::new));
    }

    public Mono<Content> get(Flux<String> ids) {
        return repository.findById(ids);
    }

    public Flux<Content> getByAccount(Mono<String> account) {
        return repository.findAllByAccountEquals(account);
    }

}
