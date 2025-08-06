package com.app.keywordwatcher.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String userId;
    private String email;
    private String password;
    private String confirmPassword;
}
