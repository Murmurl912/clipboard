package com.example.clipboard.client.repository;

import com.example.clipboard.client.entity.Content;
import org.springframework.data.repository.CrudRepository;

public interface LocalContentRepository extends CrudRepository<Content, String> {
}
