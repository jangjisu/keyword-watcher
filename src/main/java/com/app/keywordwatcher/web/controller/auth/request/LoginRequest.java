package com.app.keywordwatcher.web.controller.auth.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotEmpty(message = "사용자 ID는 필수입니다.")
    private String userId;
    @NotEmpty(message = "비밀번호는 필수입니다.")
    private String password;

    @Builder
    public LoginRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
