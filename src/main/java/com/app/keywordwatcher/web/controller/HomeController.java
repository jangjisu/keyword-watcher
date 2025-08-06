package com.app.keywordwatcher.web.controller;

import com.app.keywordwatcher.web.dto.ApiResponse;
import com.app.keywordwatcher.web.dto.UserResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping
    public String home() {
        return "index";
    }

    @GetMapping("/api/user")
    @ResponseBody
    public ApiResponse<UserResponse> getUserInfo(Authentication authentication) {
        UserResponse userResponse;
        if (authentication != null) {
            userResponse = new UserResponse(authentication.getName(), true);
        } else {
            userResponse = new UserResponse(null, false);
        }
        return ApiResponse.success(userResponse);
    }
}
