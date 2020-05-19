package com.clipboard.clipboard_store.endpoint;

import com.clipboard.clipboard_store.endpoint.model.ContentCreateModel;
import com.clipboard.clipboard_store.endpoint.model.ContentStarModel;
import com.clipboard.clipboard_store.endpoint.model.ContentStateModel;
import com.clipboard.clipboard_store.endpoint.model.ContentTextModel;
import com.clipboard.clipboard_store.event.ClipboardEvent;
import com.clipboard.clipboard_store.repository.entity.ClipboardContent;
import com.clipboard.clipboard_store.service.ClipboardEventPublisher;
import com.clipboard.clipboard_store.service.ClipboardService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
public class ClipboardEndpoint {


    private final ClipboardEventPublisher publisher;
    private final ClipboardService service;

    public ClipboardEndpoint(ClipboardEventPublisher publisher,
                             ClipboardService service) {
        this.publisher = publisher;
        this.service = service;
    }

    @GetMapping(value = "/clipboard/account/{account}/content/versions",
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ClipboardContent> versions(@PathVariable String account) {
        return service.versions(account);
    }

    @GetMapping(value = "/clipboard/account/{account}/content/{content}/version",
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ClipboardContent> version(@PathVariable String account,
                                          @PathVariable String content) {
        return service.version(content);
    }

    @GetMapping(value = "/clipboard/account/{account}/contents",
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Flux<ClipboardContent> gets(@PathVariable String account,
                                       @RequestParam(required = false) Boolean star) {
        if(star == null) {
            return service.gets(account);
        } else {
            return service.gets(account, star);
        }
    }

    @GetMapping("/clipboard/account/{account}/content/{content}")
    public Mono<ClipboardContent> get(@PathVariable String account, @PathVariable String content) {
        return service.get(content);
    }

    @PostMapping("/clipboard/account/{account}/content")
    public Mono<ClipboardContent> create(@PathVariable String account,
                                         @RequestBody @Valid ContentCreateModel model) {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.content = model.content;
        clipboardContent.contentVersion = model.contentVersion;
        clipboardContent.star = model.star;
        clipboardContent.starVersion = model.starVersion;
        clipboardContent.state = model.state;
        clipboardContent.stateVersion = model.stateVersion;
        clipboardContent.create = model.create;
        clipboardContent.update = model.update;

        return service.create(account, clipboardContent);
    }

    @PatchMapping("/clipboard/account/{account}/content/{content}/star")
    public Mono<ClipboardContent> star(@PathVariable String account,
                                       @PathVariable String content,
                                       @RequestBody @Valid ContentStarModel starModel) {
        return service.star(content, starModel.star, starModel.starVersion);
    }


    @PatchMapping("/clipboard/account/{account}/content/{content}/state")
    public Mono<ClipboardContent> delete(@PathVariable String account,
                                         @PathVariable String content,
                                         @RequestBody @Valid ContentStateModel stateModel) {
        return service.state(
                content,
                ClipboardContent.ContentState.get(stateModel.state),
                stateModel.stateVersion);
    }

    @PatchMapping("/clipboard/account/{account}/content/{content}/text")
    public Mono<ClipboardContent> text(@PathVariable String account,
                                         @PathVariable String content,
                                         @RequestBody @Valid ContentTextModel textModel) {
        return service.content(
                content,
                textModel.content,
                textModel.contentVersion);
    }

    @GetMapping(value = "/clipboard/account/{account}/event",
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ClipboardEvent> listen(@PathVariable String account,
                                       WebSession webSession) {
        return Flux.create(publisher).share()
                .filter(clipboardEvent -> !clipboardEvent.getSource().equals(account));
    }
}
