package com.app.keywordwatcher.web.controller.user.site;

import com.app.keywordwatcher.web.controller.ControllerTestSupport;
import com.app.keywordwatcher.web.controller.user.site.request.SiteRequest;
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

class UserSiteControllerTest extends ControllerTestSupport {
    private final Credentials testCredential = Credentials.of("tester", "password123");

    @DisplayName("올바른 사이트 등록 요청 시 성공 응답을 반환한다")
    @Test
    void site_add_success() throws Exception {
        // given
        SiteRequest request = SiteRequest.builder()
                .url("https://example.com")
                .build();

        Authentication authentication = new TestingAuthenticationToken("test@example.com", testCredential);
        given(userSiteService.addSite(anyString(), any(SiteRequest.class)))
                .willReturn(1L);

        // when & then
        mockMvc.perform(
                        post("/api/user/site/add")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").value(1L));
    }

    @DisplayName("url이 빈 사이트 등록 요청 시 예외가 발생한다")
    @Test
    void site_add_failure_empty_url() throws Exception {
        // given
        Authentication authentication = new TestingAuthenticationToken("test@example.com", testCredential);
        SiteRequest request = SiteRequest.builder().build();

        // when & then
        mockMvc.perform(
                        post("/api/user/site/add")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("사이트 URL은 필수입니다."));
    }

    @DisplayName("사이트 삭제 요청 시 성공 응답을 반환한다")
    @Test
    void site_delete_success() throws Exception {
        // given
        Authentication authentication = new TestingAuthenticationToken("test@example.com", testCredential);
        SiteRequest request = SiteRequest.builder()
                .url("https://example.com")
                .build();

        given(userSiteService.removeSite(anyString(), any(SiteRequest.class)))
                .willReturn(1L);

        // when & then
        mockMvc.perform(
                        post("/api/user/site/delete")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data").value(1L));
    }

    @DisplayName("url이 빈 사이트 삭제 요청 시 예외가 발생한다")
    @Test
    void site_delete_failure_empty_url() throws Exception {
        // given
        Authentication authentication = new TestingAuthenticationToken("test@example.com", testCredential);
        SiteRequest request = SiteRequest.builder().build();

        // when & then
        mockMvc.perform(
                        post("/api/user/site/delete")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(authentication)
                )
                .andDo(print())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("사이트 URL은 필수입니다."));
    }
}
