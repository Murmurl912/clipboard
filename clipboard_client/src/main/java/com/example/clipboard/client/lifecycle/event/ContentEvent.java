package com.example.clipboard.client.lifecycle.event;

import com.example.clipboard.client.repository.entity.Content;

public class ContentEvent extends AppEvent {

    private final Content content;
    public ContentEvent(String id,
                        Content content) {
        super(id);
        this.content = content;
    }

    public Content getContent() {
        return content;
    }

    @Override
    public String getSource() {
        return (String)super.getSource();
    }

}
