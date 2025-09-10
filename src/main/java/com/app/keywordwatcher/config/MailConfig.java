package com.app.keywordwatcher.config;

import com.app.keywordwatcher.mail.CustomMailSender;
import com.app.keywordwatcher.mail.MailMessageMaker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@RequiredArgsConstructor
public class MailConfig {
    private final JavaMailSender javaMailSender;

    @Bean
    public CustomMailSender customMailSender() {
        return new CustomMailSender(javaMailSender);
    }

    @Bean
    public MailMessageMaker mailMessageMaker() {
        return new MailMessageMaker();
    }
}
