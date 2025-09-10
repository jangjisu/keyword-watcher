package com.app.keywordwatcher.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

class CustomMailSenderTest {

    @Mock
    private JavaMailSender javaMailSender;

    private CustomMailSender customMailSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customMailSender = new CustomMailSender(javaMailSender);
    }

    @DisplayName("메일을 전송한다.")
    @Test
    void sendMail() {
        // given
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("test@example.com");
        message.setSubject("테스트 제목");
        message.setText("테스트 내용");

        doNothing().when(javaMailSender).send(message);

        // when
        customMailSender.sendMail(message);

        // then
        verify(javaMailSender).send(message);
    }
}