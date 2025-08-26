package com.app.keywordwatcher.web.controller.user;

import com.app.keywordwatcher.web.controller.ControllerTestSupport;
import com.zaxxer.hikari.util.Credentials;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTestSupport {
    private final Credentials testCredential = Credentials.of("tester", "password123");

    @DisplayName("인증된 사용자의 정보를 조회할 수 있다")
    @Test
    void getUserInfo() throws Exception {
        // given
        Authentication authentication = new TestingAuthenticationToken("testuser@example.com", testCredential);

        // when & then
        MockHttpServletRequestBuilder requestBuilder = get("/api/user")
                .requestAttr("authentication", authentication)
                .principal(authentication);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.userId").value("testuser@example.com"));
    }
}