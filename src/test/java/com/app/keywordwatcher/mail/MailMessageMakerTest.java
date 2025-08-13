package com.app.keywordwatcher.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;

import static org.assertj.core.api.Assertions.assertThat;

class MailMessageMakerTest {

    private final MailMessageMaker mailMessageMaker = new MailMessageMaker();

    @DisplayName("크롤링 테스트 실패 메일 메시지를 올바르게 생성한다")
    @Test
    void makeCrawlingTestFailMessage() {
        // given
        String userId = "testUser";
        String url = "https://example.com";

        // when
        SimpleMailMessage message = mailMessageMaker.makeCrawlingTestFailMessage(userId, url);

        // then
        assertThat(message.getTo()).containsExactly(MailMessageMaker.ADMIN_MAIL_ADDRESS);
        assertThat(message.getSubject()).isEqualTo("크롤링 테스트 실패 알림");
        assertThat(message.getText()).isEqualTo("userId: " + userId + "의 크롤링 테스트가 실패했습니다.\n요청 URL: " + url);
    }

    @DisplayName("새 글 알림 메일 메시지를 올바르게 생성한다")
    @Test
    void makeNewPostAlertMessage() {
        // given
        String userEmail = "test@example.com";
        String siteUrl = "https://test.com";
        String postTitle = "새로운 게시글";

        // when
        SimpleMailMessage message = mailMessageMaker.makeNewPostAlertMessage(userEmail, siteUrl, postTitle);

        // then
        assertThat(message.getTo()).containsExactly(userEmail);
        assertThat(message.getSubject()).isEqualTo("[" + siteUrl + "] 에 새 글 " + postTitle + "이 등록되었습니다");
        assertThat(message.getText()).isEqualTo("[" + siteUrl + "] 에 새 글 " + postTitle + "이 등록되었습니다");
    }
}
