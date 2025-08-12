package com.app.keywordwatcher.web.controller.user;

import com.app.keywordwatcher.web.controller.ApiResponse;
import com.app.keywordwatcher.web.controller.user.response.UserResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/user")
    public ApiResponse<UserResponse> getUserInfo(Authentication authentication) {
        return ApiResponse.success(UserResponse.create(authentication.getName()));
    }
}
