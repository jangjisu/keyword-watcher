package com.app.keywordwatcher.web.service.auth;

import com.app.keywordwatcher.domain.user.User;
import com.app.keywordwatcher.domain.user.UserRepository;
import com.app.keywordwatcher.web.controller.auth.request.LoginRequest;
import com.app.keywordwatcher.web.controller.auth.request.SignupRequest;
import com.app.keywordwatcher.web.exception.LoginException;
import com.app.keywordwatcher.web.service.ServiceTestSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Transactional
class AuthServiceTest extends ServiceTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private HttpSession httpSession;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userRepository, authenticationManager, passwordEncoder);

        User user = User.create("existUser", "exist@example.com", "password123", passwordEncoder);
        userRepository.save(user);
    }

    @DisplayName("올바른 회원가입 정보로 요청 시 사용자가 생성되고 ID를 반환한다")
    @Test
    void signUp_success() {
        // given
        SignupRequest request = SignupRequest.builder()
                .userId("newUser")
                .email("test@example.com")
                .password("password123")
                .confirmPassword("password123")
                .build();

        // when
        Long seq = authService.signUp(request);

        Optional<User> newUser = userRepository.findByUserId("newUser");

        // then
        assertThat(newUser).isPresent();

        assertThat(newUser.get())
                .extracting("id", "userId", "email")
                .containsExactly(seq, "newUser", "test@example.com");

    }

    @DisplayName("비밀번호가 일치하지 않는 경우 예외가 발생한다")
    @Test
    void signUp_passwordMismatch() {
        // given
        SignupRequest request = SignupRequest.builder()
                .userId("newUser")
                .email("test@example.com")
                .password("password123")
                .confirmPassword("password456")
                .build();

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @DisplayName("이미 사용 중인 아이디인 경우 예외가 발생한다")
    @Test
    void signUp_duplicateUserId() {
        // given
        SignupRequest request = SignupRequest.builder()
                .userId("existUser")
                .email("test@example.com")
                .password("password123")
                .confirmPassword("password123")
                .build();

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 ID입니다.");
    }

    @DisplayName("이미 사용 중인 이메일인 경우 예외가 발생한다")
    @Test
    void signUp_duplicateEmail() {
        // given
        SignupRequest request = SignupRequest.builder()
                .userId("testuser")
                .email("exist@example.com")
                .password("password123")
                .confirmPassword("password123")
                .build();

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 이메일입니다.");
    }

    @DisplayName("올바른 로그인 정보로 요청 시 인증이 성공한다")
    @Test
    void login_success() {
        // given
        LoginRequest request = LoginRequest.builder()
                .userId("existUser")
                .password("password123")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(httpServletRequest.getSession(true)).thenReturn(httpSession);

        // when
        authService.login(request, httpServletRequest);

        // then
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(httpServletRequest).getSession(true);
        verify(httpSession).setAttribute(eq("SPRING_SECURITY_CONTEXT"), any());
    }

    @DisplayName("잘못된 로그인 정보로 요청 시 LoginException이 발생한다")
    @Test
    void login_failure() {
        // given
        LoginRequest request = LoginRequest.builder()
                .userId("wronguser")
                .password("wrongpassword")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("인증 실패"));

        // when & then
        assertThatThrownBy(() -> authService.login(request, httpServletRequest))
                .isInstanceOf(LoginException.class)
                .hasMessage("로그인에 실패했습니다.");
    }

    @DisplayName("로그아웃 시 세션이 무효화되고 SecurityContext가 초기화된다")
    @Test
    void logout_success() {
        // given
        when(httpServletRequest.getSession(false)).thenReturn(httpSession);

        // when
        authService.logout(httpServletRequest, httpServletResponse);

        // then
        verify(httpSession).invalidate();
        verify(httpServletResponse).addCookie(any());
    }

    @DisplayName("세션이 없는 상태에서 로그아웃 시에도 정상적으로 처리된다")
    @Test
    void logout_noSession() {
        // given
        when(httpServletRequest.getSession(false)).thenReturn(null);

        // when
        authService.logout(httpServletRequest, httpServletResponse);

        // then
        verify(httpServletResponse).addCookie(any());
        verifyNoInteractions(httpSession);
    }
}
