package com.example.clipboard.server.repository;

import com.example.clipboard.server.entity.temp.VerificationCode;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends ReactiveMongoRepository<VerificationCode, String> {

}
