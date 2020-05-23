package com.example.clipboard.server.repository;

import com.example.clipboard.server.entity.temp.VerificationRecord;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface VerificationRecordRepository extends ReactiveMongoRepository<VerificationRecord, String> {

    public Mono<VerificationRecord> findVerificationRecordByAccountEquals(String account);
}
