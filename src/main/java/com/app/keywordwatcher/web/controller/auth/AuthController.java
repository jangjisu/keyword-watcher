package com.app.keywordwatcher.web.controller.auth;

import com.app.keywordwatcher.web.controller.ApiResponse;
import com.app.keywordwatcher.web.dto.LoginRequest;
import com.app.keywordwatcher.web.dto.SignupRequest;
import com.app.keywordwatcher.web.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/public")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/login")
    public ApiResponse<Boolean> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        authService.login(request, httpRequest);
        return ApiResponse.success(true);
    }

    @PostMapping("/api/signup")
    public ApiResponse<Long> signup(@RequestBody SignupRequest request) {
        return ApiResponse.success(authService.signUp(request));
    }

    @PostMapping("/api/logout")
    public ApiResponse<Boolean> logout(HttpServletRequest request) {
        authService.logout(request);

        return ApiResponse.success(true);
    }
}
