package com.app.keywordwatcher.web.controller;

import com.app.keywordwatcher.web.controller.auth.AuthController;
import com.app.keywordwatcher.web.controller.user.UserController;
import com.app.keywordwatcher.web.service.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

@WebMvcTest(controllers = {
        AuthController.class,
        UserController.class,
})
@Import(ControllerTestSupport.Mocks.class)
@AutoConfigureMockMvc(addFilters = false) // spring security 필터를 비활성화
public abstract class ControllerTestSupport {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected AuthService authService;

    @TestConfiguration
    static class Mocks {
        @Bean
        AuthService authService() {
            return mock(AuthService.class);
        }
    }

    @BeforeEach
    void resetMocks() {
        reset(authService);
    }
}

