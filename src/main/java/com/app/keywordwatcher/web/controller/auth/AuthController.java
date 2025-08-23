package com.app.keywordwatcher.web.controller.auth;

import com.app.keywordwatcher.web.controller.ApiResponse;
import com.app.keywordwatcher.web.controller.auth.request.LoginRequest;
import com.app.keywordwatcher.web.controller.auth.request.SignupRequest;
import com.app.keywordwatcher.web.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/public/api")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<Boolean> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        authService.login(request, httpRequest);
        return ApiResponse.success(true);
    }

    @PostMapping("/signup")
    public ApiResponse<Long> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.success(authService.signUp(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);

        return ApiResponse.success(true);
    }
}
