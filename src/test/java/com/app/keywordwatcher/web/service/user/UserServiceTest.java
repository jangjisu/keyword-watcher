package com.app.keywordwatcher.web.service.user;

import com.app.keywordwatcher.domain.user.User;
import com.app.keywordwatcher.domain.user.UserRepository;
import com.app.keywordwatcher.web.controller.auth.request.SignupRequest;
import com.app.keywordwatcher.web.service.ServiceTestSupport;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class UserServiceTest extends ServiceTestSupport {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        User user = User.create("existUser", "exist@example.com", "password123", passwordEncoder);
        userRepository.save(user);
    }

    @DisplayName("유저 아이디로 유저정보를 검색할 수 있다.")
    @Test
    void findByUserId_success() {
        // given // when
        User existUser = userService.findByUserId("existUser");

        // then
        assertThat(existUser)
                .extracting("userId", "email")
                .containsExactly("existUser", "exist@example.com");
    }

    @DisplayName("존재하지 않는 유저 아이디로 유저정보를 검색할 경우 예외가 발생한다.")
    @Test
    void findByUserId_fail() {
        // given // when // then
        assertThatThrownBy(() -> userService.findByUserId("nonexistUser"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");

    }

    @DisplayName("유저를 생성할 수 있다.")
    @Test
    void save_success() {
        // given
        SignupRequest request = SignupRequest.builder()
                .userId("newUser")
                .email("test@example.com")
                .password("password123")
                .confirmPassword("password123")
                .build();

        // when
        Long seq = userService.save(request).getId();

        Optional<User> newUser = userRepository.findByUserId("newUser");

        // then
        assertThat(newUser).isPresent();

        assertThat(newUser.get())
                .extracting("id", "userId", "email")
                .containsExactly(seq, "newUser", "test@example.com");

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
        assertThatThrownBy(() -> userService.save(request))
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
        assertThatThrownBy(() -> userService.save(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 이메일입니다.");
    }
}