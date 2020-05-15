package com.example.clipboard.client;

import com.example.clipboard.client.repository.LocalContentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RepositoryTest {
    @Autowired
    private LocalContentRepository repository;

    @Test
    public void doTest() {
        repository.findAll();
    }
}
