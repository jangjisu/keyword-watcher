package com.app.keywordwatcher.web.controller.usersite;

import com.app.keywordwatcher.web.controller.ApiResponse;
import com.app.keywordwatcher.web.controller.site.request.SiteRequest;
import com.app.keywordwatcher.web.service.user.UserSiteService;
import com.app.keywordwatcher.web.service.user.response.SiteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/site")
public class UserSiteController {
    private final UserSiteService userSiteService;

    @GetMapping
    public ApiResponse<List<SiteResponse>> getUserSites(Authentication authentication) {
        return ApiResponse.success(userSiteService.getUserSites(authentication.getName()));
    }

    @PostMapping("/add")
    public ApiResponse<Long> addSite(@Valid @RequestBody SiteRequest siteRequest, Authentication authentication) {
        return ApiResponse.success(userSiteService.addSite(authentication.getName(), siteRequest));
    }

    @PostMapping("/delete")
    public ApiResponse<Long> deleteSite(@Valid @RequestBody SiteRequest siteRequest, Authentication authentication) {
        return ApiResponse.success(userSiteService.removeSite(authentication.getName(), siteRequest));
    }
}
