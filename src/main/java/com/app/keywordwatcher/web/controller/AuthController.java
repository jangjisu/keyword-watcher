package com.app.keywordwatcher.web.controller;

import com.app.keywordwatcher.domain.user.User;
import com.app.keywordwatcher.domain.user.UserRepository;
import com.app.keywordwatcher.web.dto.ApiResponse;
import com.app.keywordwatcher.web.dto.LoginRequest;
import com.app.keywordwatcher.web.dto.SignupRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/public")
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public ApiResponse<String> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return ApiResponse.success("로그인이 완료되었습니다.");
        } catch (Exception e) {
            return ApiResponse.error("로그인에 실패했습니다.");
        }
    }

    @PostMapping("/api/signup")
    @ResponseBody
    public ApiResponse<String> signup(@RequestBody SignupRequest request) {
        if (request.isPasswordNonMatch()) {
            return ApiResponse.error("비밀번호가 일치하지 않습니다.");
        }

        if (userRepository.existsByUserId(request.getUserId())) {
            return ApiResponse.error("이미 사용 중인 ID입니다.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.error("이미 사용 중인 이메일입니다.");
        }

        if (request.getPassword() == null) {
            return ApiResponse.error("비밀번호를 입력해주세요.");
        }

        User user = User.create(request.getUserId(), request.getEmail(), request.getPassword(), passwordEncoder);
        userRepository.save(user);

        return ApiResponse.success("회원가입이 완료되었습니다.");
    }

    @GetMapping("/api/auth/status")
    @ResponseBody
    public ApiResponse<String> getAuthStatus(@RequestParam(value = "error", required = false) String error,
                                             @RequestParam(value = "logout", required = false) String logout) {
        if (error != null) {
            return ApiResponse.error("로그인에 실패했습니다.");
        }
        if (logout != null) {
            return ApiResponse.success("성공적으로 로그아웃되었습니다.", null);
        }
        return ApiResponse.success(null);
    }

    @PostMapping("/api/logout")
    @ResponseBody
    public ApiResponse<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return ApiResponse.success("로그아웃이 완료되었습니다.");
    }
}
