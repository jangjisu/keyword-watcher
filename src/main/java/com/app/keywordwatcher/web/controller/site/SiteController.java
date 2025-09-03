package com.app.keywordwatcher.web.controller.site;

import com.app.keywordwatcher.web.controller.ApiResponse;
import com.app.keywordwatcher.web.controller.site.request.SiteRequest;
import com.app.keywordwatcher.web.service.site.SiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/site")
public class SiteController {
    private final SiteService siteService;

    @PostMapping("/test")
    public ApiResponse<Boolean> testSite(@Valid @RequestBody SiteRequest siteRequest) {
        return ApiResponse.success(siteService.testUrl(siteRequest));
    }
}
