package com.app.keywordwatcher.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@RequiredArgsConstructor
public class CustomMailSender {
    private final JavaMailSender javaMailSender;

    public void sendMail(SimpleMailMessage message) {
        javaMailSender.send(message);
    }
}
