package com.example.clipboard.server.service.reactive;


import com.example.clipboard.server.service.evnet.SendMailEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.mail.internet.MimeMessage;
import java.util.Objects;

@Service
public class MailReactiveService implements ApplicationListener<SendMailEvent> {

    private final JavaMailSenderImpl mailSender;

    public MailReactiveService(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(SendMailEvent event) {
        Mono.fromCallable(()->{
            send(event);
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic()).subscribe();
    }

    private void send(@NonNull SendMailEvent event) {
        MailMessage message = event.getMailMessage();
        message.setFrom(Objects.requireNonNull(mailSender.getUsername()));
        if (message instanceof SimpleMailMessage) {
            mailSender.send((SimpleMailMessage) message);
        } else if (message instanceof MimeMessage) {
            mailSender.send((MimeMessage)message);
        }
        // todo: handle error
    }


    public Mono<Void> send(@NonNull Mono<SimpleMailMessage> message) {
        return message
                .map(simple -> {
                    simple.setFrom(Objects.requireNonNull(mailSender.getUsername()));
                    return simple;
                })
                .map(mail -> {
                    mailSender.send(mail);
                    return Mono.empty();
                }).subscribeOn(Schedulers.boundedElastic())
                .then();
    }


}
