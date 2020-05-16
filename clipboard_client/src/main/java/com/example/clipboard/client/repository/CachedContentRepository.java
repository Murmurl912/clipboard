package com.example.clipboard.client.repository;

import com.example.clipboard.client.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CachedContentRepository extends JpaRepository<Content, String> {

}
