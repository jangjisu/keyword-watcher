package com.app.keywordwatcher.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
@RequiredArgsConstructor
public class MailSender {
    private final JavaMailSender javaMailSender;

    public void sendMail(SimpleMailMessage message) {
        try {
            javaMailSender.send(message);
        } catch (RuntimeException e) {
            String to = message.getTo() == null ? "No recipients" : String.join(", ", message.getTo());

            String errorMsg = String.format(
                    "Subject: %s, TO : %s, ClassType: %s, Error: %s",
                    message.getSubject(),
                    to,
                    message.getClass().getName(),
                    e.getMessage()
            );

            throw new MailSendException(errorMsg);
        }

    }
}
