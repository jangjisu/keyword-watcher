package com.app.keywordwatcher.mail.format;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NewPostAlertMessageTest {
    @DisplayName("새 글 알림 메일 메시지를 올바르게 생성한다")
    @Test
    void makeNewPostAlertMessage() {
        // given
        String userEmail = "test@example.com";
        String siteUrl = "https://test.com";
        String postTitle = "새로운 게시글";

        // when
        NewPostAlertMessage message = NewPostAlertMessage.create(userEmail, siteUrl, postTitle);

        // then
        assertThat(message.getTo()).containsExactly(userEmail);
        assertThat(message.getSubject()).isEqualTo("[" + siteUrl + "] 에 새 글 " + postTitle + "이 등록되었습니다");
        assertThat(message.getText()).isEqualTo("[" + siteUrl + "] 에 새 글 " + postTitle + "이 등록되었습니다");
    }
}