package com.app.keywordwatcher.web.controller.auth.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignupRequest {
    @NotEmpty(message = "사용자 ID는 필수입니다.")
    private String userId;
    @NotEmpty(message = "이메일은 필수입니다.")
    private String email;
    @NotEmpty(message = "비밀번호는 필수입니다.")
    private String password;
    @NotEmpty(message = "비밀번호 확인은 필수입니다.")
    private String confirmPassword;

    public boolean isPasswordNonMatch() {
        return !password.equals(confirmPassword);
    }

    @Builder
    public SignupRequest(String userId, String email, String password, String confirmPassword) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
