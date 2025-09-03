package com.app.keywordwatcher.web.controller;

import com.app.keywordwatcher.web.controller.auth.AuthController;
import com.app.keywordwatcher.web.controller.site.SiteController;
import com.app.keywordwatcher.web.controller.user.UserController;
import com.app.keywordwatcher.web.controller.userkeyword.UserKeywordController;
import com.app.keywordwatcher.web.controller.usersite.UserSiteController;
import com.app.keywordwatcher.web.service.auth.AuthService;
import com.app.keywordwatcher.web.service.site.SiteService;
import com.app.keywordwatcher.web.service.user.UserKeywordService;
import com.app.keywordwatcher.web.service.user.UserSiteService;
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
        SiteController.class,
        UserController.class,
        UserKeywordController.class,
        UserSiteController.class,
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

    @Autowired
    protected UserKeywordService userKeywordService;

    @Autowired
    protected UserSiteService userSiteService;

    @Autowired
    protected SiteService siteService;

    @TestConfiguration
    static class Mocks {
        @Bean
        AuthService authService() {
            return mock(AuthService.class);
        }

        @Bean
        UserKeywordService userKeywordService() {
            return mock(UserKeywordService.class);
        }

        @Bean
        UserSiteService userSiteService() {
            return mock(UserSiteService.class);
        }

        @Bean
        SiteService siteService() {
            return mock(SiteService.class);
        }
    }

    @BeforeEach
    void resetMocks() {
        reset(authService, userKeywordService, userSiteService, siteService);
    }
}
