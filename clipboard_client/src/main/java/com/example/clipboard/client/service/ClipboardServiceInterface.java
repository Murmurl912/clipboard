package com.example.clipboard.client.service;

import com.example.clipboard.client.repository.entity.Content;

import java.util.List;
import java.util.Optional;

public interface ClipboardServiceInterface {

    Content create(String text);

    Content star(String id, boolean star);

    Content state(String id, Content.ContentState state);

    Content text(String id, String text);

    Optional<Content> get(String id);

    List<Content> stars();

    List<Content> clipboard();

}
