package com.app.keywordwatcher.web.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserResponse {
    private String username;

    public static UserResponse create(String username) {
        return new UserResponse(username);
    }
}
