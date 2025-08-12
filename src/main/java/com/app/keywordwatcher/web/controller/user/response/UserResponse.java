package com.app.keywordwatcher.web.controller.user.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserResponse {
    private String userId;

    public static UserResponse create(String userId) {
        return new UserResponse(userId);
    }
}
