package com.app.keywordwatcher.web.controller.auth;

import com.app.keywordwatcher.web.controller.ControllerTestSupport;
import com.app.keywordwatcher.web.controller.auth.request.LoginRequest;
import com.app.keywordwatcher.web.controller.auth.request.SignupRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends ControllerTestSupport {

    @DisplayName("올바른 로그인 요청 시 성공 응답을 반환한다")
    @Test
    void login_success() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .userId("newuser")
                .password("password123")
                .build();

        doNothing().when(authService).login(any(LoginRequest.class), any(HttpServletRequest.class));

        // when & then
        mockMvc.perform(
                        post("/public/api/login")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("200 OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @DisplayName("userId가 빈 로그인 요청 시 예외가 발생한다")
    @Test
    void login_failure_username_empty() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .userId("")
                .password("password123")
                .build();

        // when & then
        mockMvc.perform(
                        post("/public/api/login")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("사용자 ID는 필수입니다."));
    }

    @DisplayName("password가 빈 로그인 요청 시 예외가 발생한다")
    @Test
    void login_failure_password_empty() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .userId("newuser")
                .password("")
                .build();

        // when & then
        mockMvc.perform(
                        post("/public/api/login")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("비밀번호는 필수입니다."));
    }

    @DisplayName("올바른 회원가입 요청 시 생성된 사용자 ID를 반환한다")
    @Test
    void signup_success() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .userId("newuser")
                .email("new@example.com")
                .password("password123")
                .confirmPassword("password123")
                .build();

        // when & then
        mockMvc.perform(post("/public/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("200 OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("userId가 빈 회원가입 요청 시 예외가 발생한다")
    @Test
    void signup_failure_userId_empty() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .userId("")
                .email("new@example.com")
                .password("password123")
                .confirmPassword("password123")
                .build();

        // when & then
        mockMvc.perform(
                        post("/public/api/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("사용자 ID는 필수입니다."));
    }

    @DisplayName("email이 빈 회원가입 요청 시 예외가 발생한다")
    @Test
    void signup_failure_email_empty() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .userId("newuser")
                .email("")
                .password("password123")
                .confirmPassword("password123")
                .build();

        // when & then
        mockMvc.perform(
                        post("/public/api/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("이메일은 필수입니다."));
    }

    @DisplayName("password가 빈 회원가입 요청 시 예외가 발생한다")
    @Test
    void signup_failure_password_empty() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .userId("newuser")
                .email("new@example.com")
                .password("")
                .confirmPassword("password123")
                .build();

        // when & then
        mockMvc.perform(
                        post("/public/api/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("비밀번호는 필수입니다."));
    }

    @DisplayName("confirmPassword가 빈 회원가입 요청 시 예외가 발생한다")
    @Test
    void signup_failure_passwordConfirm_empty() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .userId("newuser")
                .email("new@example.com")
                .password("password123")
                .confirmPassword("")
                .build();

        // when & then
        mockMvc.perform(
                        post("/public/api/signup")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("비밀번호 확인은 필수입니다."));
    }

    @DisplayName("로그아웃 요청 시 성공 응답을 반환한다")
    @Test
    void logout_success() throws Exception {
        // given // when & then
        mockMvc.perform(
                        post("/public/api/logout"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("200 OK"))
                .andExpect(jsonPath("$.data").value(true));
    }
}
