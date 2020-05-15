package com.example.clipboard.server.service.reactive;



import com.example.clipboard.server.entity.temp.VerificationCode;
import com.example.clipboard.server.entity.temp.VerificationRecord;
import com.example.clipboard.server.exception.RequestBessyException;
import com.example.clipboard.server.helper.RandomHelper;
import com.example.clipboard.server.repository.VerificationCodeRepository;
import com.example.clipboard.server.repository.VerificationRecordRepository;
import com.example.clipboard.server.service.evnet.SendMailEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class VerificationCodeReactiveService {

    private final VerificationCodeRepository codeRepository;
    private final VerificationRecordRepository recordRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public VerificationCodeReactiveService(ApplicationEventPublisher applicationEventPublisher,
                                           VerificationCodeRepository codeRepository,
                                           VerificationRecordRepository recordRepository) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.codeRepository = codeRepository;
        this.recordRepository = recordRepository;
    }

    public Mono<Boolean> verify(@NonNull String id, @NonNull String code,
                                @NonNull VerificationCode.VerificationCodeType type) {
        return codeRepository.findById(id)
                .defaultIfEmpty(new VerificationCode())  // todo reimplement
                .flatMap(truth -> {
                    if(truth.id == null) { // when no record is found
                       return  Mono.just(false);
                    }

                    if(type.TYPE != truth.type) {
                        return Mono.just(false);
                    }

                    if(!truth.active) {
                        return Mono.just(false);
                    }

                    if(truth.expire.before(new Date())) {
                        return Mono.just(false);
                    }

                    return Mono.just(truth.code.equals(code));
                });
    }

    public Mono<Void> remove(@NonNull String id) {
        return codeRepository.deleteById(id);
    }

    public Mono<VerificationCode> send(@NonNull String account,
                                       @NonNull String email,
                                       @NonNull VerificationCode.VerificationCodeType type) {
        return recordRepository.findVerificationRecordByAccountEquals(account)
                .defaultIfEmpty(new VerificationRecord()) // todo re implement
                .handle((record, sink) -> {
                    if(record.account != null && record.latest.getTime() > System.currentTimeMillis() - 1000 * 60) {
                        sink.error(new RequestBessyException());
                    } else {
                        VerificationCode code = new VerificationCode();
                        code.active = true;
                        code.create = new Date();
                        code.expire = new Date(System.currentTimeMillis() + 10 * 60 * 1000);
                        code.account = account;
                        code.destination = email;
                        code.type = type.TYPE;
                        code.code = RandomHelper.randomCode(6).toUpperCase();
                        sink.next(code);
                    }
                })
                .cast(VerificationCode.class)
                .flatMap(codeRepository::insert)
                .map(code -> {
                    String message = "[EXPIRED AFTER " + code.expire + "] Email Verification Code: " + code.code;
                    String subject = "Email Verification[No reply]";
                    SimpleMailMessage mailMessage  = new SimpleMailMessage();
                    mailMessage.setSubject(subject);
                    mailMessage.setTo(code.destination);
                    mailMessage.setText(message);
                    applicationEventPublisher.publishEvent(new SendMailEvent(code, mailMessage));
                    return code;
                });
    }

}
