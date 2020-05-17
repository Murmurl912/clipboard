package com.example.clipboard.client.repository;

import com.example.clipboard.client.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CachedContentRepository extends JpaRepository<Content, String> {

    public Long countContentByIdEquals(String id);

    public List<Content> getContentsByStateEquals(Integer state);

    public List<Content> getContentsByStateIn(Collection<Integer> state);

    public Optional<Content> findContentByContentEqualsAndHashEquals(String content, String hash);

}
