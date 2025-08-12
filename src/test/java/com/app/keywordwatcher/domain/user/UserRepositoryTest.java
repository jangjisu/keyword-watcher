package com.app.keywordwatcher.domain.user;

import com.app.keywordwatcher.domain.RepositoryTestSupport;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class UserRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        User user = User.create("testuser", "test@example.com", "password123", passwordEncoder);
        userRepository.save(user);
        entityManager.flush();
    }

    @DisplayName("사용자 ID로 사용자를 찾을 수 있다")
    @Test
    void findByUserId() {
        // given // when
        Optional<User> foundUser = userRepository.findByUserId("testuser");

        // then
        assertThat(foundUser).isPresent();

        assertThat(foundUser.get())
                .extracting("userId", "email")
                .containsExactly("testuser", "test@example.com");
    }

    @DisplayName("존재하지 않는 사용자 ID로 조회 시 빈 Optional을 반환한다")
    @Test
    void findByUserId_notFound() {
        // when
        Optional<User> foundUser = userRepository.findByUserId("nonexistent");

        // then
        assertThat(foundUser).isEmpty();
    }

    @DisplayName("사용자 ID 존재 여부를 확인할 수 있다")
    @Test
    void existsByUserId() {
        // given // when // then
        assertThat(userRepository.existsByUserId("testuser")).isTrue();
        assertThat(userRepository.existsByUserId("nonexistent")).isFalse();
    }

    @DisplayName("이메일 존재 여부를 확인할 수 있다")
    @Test
    void existsByEmail() {
        // given // when // then
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }
}
