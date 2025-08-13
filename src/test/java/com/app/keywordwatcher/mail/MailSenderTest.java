package com.app.keywordwatcher.mail;

import com.app.keywordwatcher.mail.format.CrawlingFailMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MailSenderTest {

    @Mock
    private JavaMailSender javaMailSender;

    private MailSender mailSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mailSender = new MailSender(javaMailSender);
    }

    @DisplayName("메일을 전송한다.")
    @Test
    void sendMail() {
        // given
        CrawlingFailMessage message = CrawlingFailMessage.create("testUser", "https://example.com");

        doNothing().when(javaMailSender).send(message);

        // when
        mailSender.sendMail(message);

        // then
        verify(javaMailSender).send(message);
    }

    @DisplayName("메일 전송 실패 시 SendMailException을 발생 시키며, Subject, URL, TO, ClassType 정보를 포함한다.")
    @Test
    void sendMailException() {
        // given
        String url = "https://example.com";
        String testUser = "testUser";
        CrawlingFailMessage message = CrawlingFailMessage.create(testUser, url);

        doThrow(new RuntimeException("메일 전송 실패")).when(javaMailSender).send(message);

        // when & then
        assertThatThrownBy(() -> mailSender.sendMail(message))
                .isInstanceOf(MailSendException.class)
                .hasMessageContaining("Subject")
                .hasMessageContaining("URL")
                .hasMessageContaining("TO")
                .hasMessageContaining("ClassType");
    }
}