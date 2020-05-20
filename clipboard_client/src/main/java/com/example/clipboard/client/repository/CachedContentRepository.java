package com.example.clipboard.client.repository;

import com.example.clipboard.client.repository.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CachedContentRepository extends JpaRepository<Content, String> {

    Long countContentByIdEquals(String id);

    List<Content> getContentsByStateEquals(Integer state);

    List<Content> getContentsByStateIn(Collection<Integer> state);


    List<Content> getContentsByStateEqualsAndStarEquals(Integer state, Boolean star);

    Optional<Content> findContentByHashEquals(byte[] hash);
}
