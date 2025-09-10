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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.reset;

@WebMvcTest(controllers = {
        AuthController.class,
        SiteController.class,
        UserController.class,
        UserKeywordController.class,
        UserSiteController.class,
})
@AutoConfigureMockMvc(addFilters = false) // spring security 필터 비활성화
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;


    protected ObjectMapper objectMapper = new ObjectMapper();

    // Service 계층은 MockBean으로 등록
    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected UserKeywordService userKeywordService;

    @MockitoBean
    protected UserSiteService userSiteService;

    @MockitoBean
    protected SiteService siteService;

    @BeforeEach
    void resetMocks() {
        reset(authService, userKeywordService, userSiteService, siteService);
    }
}
