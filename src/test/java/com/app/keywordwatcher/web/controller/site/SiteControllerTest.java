package com.app.keywordwatcher.web.controller.site;

import com.app.keywordwatcher.web.controller.ControllerTestSupport;
import com.app.keywordwatcher.web.controller.site.request.SiteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SiteControllerTest extends ControllerTestSupport {

    @DisplayName("올바른 URL로 사이트 테스트 요청 시 성공 응답을 반환한다")
    @Test
    void site_test_success() throws Exception {
        // given
        SiteRequest request = SiteRequest.builder()
                .url("https://culture.seoul.go.kr/culture/bbs/B0000002/list.do?menuNo=200052")
                .build();

        given(siteService.testUrl(any(SiteRequest.class)))
                .willReturn(true);

        // when & then
        mockMvc.perform(
                        post("/api/site/test")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("200 OK"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @DisplayName("URL이 없는 사이트 테스트 요청 시 400 에러를 반환한다")
    @Test
    void site_test_failure_empty_url() throws Exception {
        // given
        SiteRequest request = SiteRequest.builder().build();

        // when & then
        mockMvc.perform(
                        post("/api/site/test")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("사이트 URL은 필수입니다."));
    }
}