package com.app.keywordwatcher.mail.format;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CrawlingFailMessageTest {
    @DisplayName("크롤링 테스트 실패 메일 메시지를 올바르게 생성한다")
    @Test
    void makeCrawlingTestFailMessage() {
        // given
        String userId = "testUser";
        String url = "https://example.com";

        // when
        CrawlingFailMessage message = CrawlingFailMessage.create(userId, url);

        // then
        assertThat(message.getTo()).containsExactly(CrawlingFailMessage.ADMIN_MAIL_ADDRESS);
        assertThat(message.getSubject()).isEqualTo("userId: " + userId + "의 URL : " + url + " 크롤링 테스트가 실패했습니다.");
        assertThat(message.getText()).isEqualTo("userId: " + userId + "의 크롤링 테스트가 실패했습니다.\n요청 URL: " + url);
    }
}