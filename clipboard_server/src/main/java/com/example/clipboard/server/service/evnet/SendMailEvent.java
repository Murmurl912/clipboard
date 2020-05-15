package com.example.clipboard.server.service.evnet;

import org.springframework.context.ApplicationEvent;
import org.springframework.mail.MailMessage;

public class SendMailEvent extends ApplicationEvent {

    private final MailMessage mailMessage;

    public SendMailEvent(Object source, MailMessage mailMessage) {
        super(source);
        this.mailMessage = mailMessage;
    }

    public MailMessage getMailMessage() {
        return mailMessage;
    }

}
