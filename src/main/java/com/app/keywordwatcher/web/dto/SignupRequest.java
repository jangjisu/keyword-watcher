package com.app.keywordwatcher.web.dto;

import lombok.*;

@Getter
public class SignupRequest {
    private String userId;
    private String email;
    private String password;
    private String confirmPassword;

    public boolean isPasswordNonMatch() {
        return !password.equals(confirmPassword);
    }
}
