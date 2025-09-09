package com.app.keywordwatcher.web.controller.userkeyword;

import com.app.keywordwatcher.web.controller.ControllerTestSupport;
import com.app.keywordwatcher.web.controller.keyword.request.KeywordRequest;
import com.zaxxer.hikari.util.Credentials;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserKeywordControllerTest extends ControllerTestSupport {
    private final Credentials testCredential = Credentials.of("tester", "password123");

    @DisplayName("올바른 키워드 등록 요청 시 성공 응답을 반환한다")
    @Test
    void keyword_add_success() throws Exception {
        // given
        KeywordRequest request = KeywordRequest.builder()
                .keyText("testKeyword")
                .build();

        Authentication authentication = new TestingAuthenticationToken("test@example.com", testCredential);
        given(userKeywordService.addKeyword(anyString(), any(KeywordRequest.class)))
                .willReturn(1L);

        // when & then
        mockMvc.perform(
                        post("/api/user/keyword/add")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("200 OK"))
                .andExpect(jsonPath("$.data").value(1L));
    }

    @DisplayName("keyword가 빈 키워드 등록 요청 시 예외가 발생한다")
    @Test
    void keyword_add_failure_empty_keyword() throws Exception {
        // given
        Authentication authentication = new TestingAuthenticationToken("test@example.com", testCredential);
        KeywordRequest request = KeywordRequest.builder()
                .build();

        // when & then
        mockMvc.perform(
                        post("/api/user/keyword/add")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("키워드는 필수입니다."));
    }

    @DisplayName("키워드 삭제 요청 시 성공 응답을 반환한다")
    @Test
    void keyword_delete_success() throws Exception {
        // given
        Authentication authentication = new TestingAuthenticationToken("test@example.com", testCredential);
        KeywordRequest request = KeywordRequest.builder()
                .keyText("testKeyword")
                .build();

        given(userKeywordService.removeKeyword(anyString(), any(KeywordRequest.class)))
                .willReturn(1L);

        // when & then
        mockMvc.perform(
                        post("/api/user/keyword/delete")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("200 OK"))
                .andExpect(jsonPath("$.data").value(1L));
    }

    @DisplayName("keyword가 빈 키워드 삭제 요청 시 예외가 발생한다")
    @Test
    void keyword_delete_failure_empty_keyword() throws Exception {
        // given
        Authentication authentication = new TestingAuthenticationToken("test@example.com", testCredential);

        KeywordRequest request = KeywordRequest.builder()
                .build();

        // when & then
        mockMvc.perform(
                        post("/api/user/keyword/delete")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("키워드는 필수입니다."));
    }
}