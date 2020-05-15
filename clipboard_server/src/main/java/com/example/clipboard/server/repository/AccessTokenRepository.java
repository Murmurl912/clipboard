package com.example.clipboard.server.repository;


import com.example.clipboard.server.entity.temp.AccessToken;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AccessTokenRepository extends ReactiveMongoRepository<AccessToken, String> {

}
