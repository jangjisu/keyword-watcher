package com.app.keywordwatcher.web.service.auth;

import com.app.keywordwatcher.exception.LoginException;
import com.app.keywordwatcher.web.controller.auth.request.LoginRequest;
import com.app.keywordwatcher.web.controller.auth.request.SignupRequest;
import com.app.keywordwatcher.web.service.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public Long signUp(SignupRequest request) {
        if (request.getPassword() == null) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        if (request.isPasswordNonMatch()) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return userService.save(request).getId();
    }

    public void login(LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        } catch (RuntimeException e) {
            log.error("Authentication failed for user: {}", request.getUserId());
            throw new LoginException("로그인에 실패했습니다.");
        }

    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
