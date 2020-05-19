package com.clipboard.clipboard_store;

import com.clipboard.clipboard_store.repository.ContentRepository;
import com.clipboard.clipboard_store.repository.entity.ClipboardContent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ClipboardStoreApplicationTests {

    @Autowired
    private ContentRepository repository;

    @Test
    void contextLoads() {
        ClipboardContent content = repository.findById("5ec33f5f40091d59ddf23143")
                .block();
        System.out.println(content);
    }

}
