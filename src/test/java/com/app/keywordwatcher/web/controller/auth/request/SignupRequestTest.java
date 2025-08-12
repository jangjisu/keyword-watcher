package com.app.keywordwatcher.web.controller.auth.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SignupRequestTest {

    @DisplayName("비밀번호와 확인 비밀번호가 일치하면 false를 반환한다")
    @Test
    void isPasswordNonMatch_match() {
        // given
        SignupRequest request = SignupRequest.builder()
                .userId("testuser")
                .email("test@example.com")
                .password("password123")
                .confirmPassword("password123")
                .build();

        // when // then
        assertThat(request.isPasswordNonMatch()).isFalse();
    }

    @DisplayName("비밀번호와 확인 비밀번호가 일치하지 않으면 true를 반환한다")
    @Test
    void isPasswordNonMatch_nonMatch() {
        // given
        SignupRequest request = SignupRequest.builder()
                .userId("testuser")
                .email("test@example.com")
                .password("password123")
                .confirmPassword("password456")
                .build();

        // when // then
        assertThat(request.isPasswordNonMatch()).isTrue();
    }
}
