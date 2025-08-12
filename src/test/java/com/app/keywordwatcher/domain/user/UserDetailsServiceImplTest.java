package com.app.keywordwatcher.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @DisplayName("존재하는 사용자 ID로 조회 시 UserDetails를 반환한다")
    @Test
    void loadUserByUsername_success() {
        // given
        String encoded = passwordEncoder.encode("password123");
        when(encoded).thenReturn("encodedPassword");
        User user = User.create("testuser", "test@example.com", "password123", passwordEncoder);
        when(userRepository.findByUserId("testuser")).thenReturn(Optional.of(user));

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails)
                .extracting("username", "password")
                .containsExactly("testuser", "encodedPassword");
        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @DisplayName("존재하지 않는 사용자 ID로 조회 시 UsernameNotFoundException이 발생한다")
    @Test
    void loadUserByUsername_userNotFound() {
        // given
        when(userRepository.findByUserId("nonexistent")).thenReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다: nonexistent");
    }
}
