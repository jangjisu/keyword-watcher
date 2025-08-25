package com.app.keywordwatcher.web.controller.user;

import com.app.keywordwatcher.web.controller.ApiResponse;
import com.app.keywordwatcher.web.controller.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    @GetMapping("/")
    public ApiResponse<UserResponse> getUserInfo(Authentication authentication) {
        return ApiResponse.success(UserResponse.create(authentication.getName()));
    }
}
