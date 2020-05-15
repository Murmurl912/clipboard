package com.example.clipboard.client.repository;

import com.example.clipboard.client.entity.AccessToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalAccessTokenRepository extends CrudRepository<AccessToken, String> {
}
