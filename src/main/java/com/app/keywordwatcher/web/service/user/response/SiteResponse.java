package com.app.keywordwatcher.web.service.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SiteResponse {
    private String url;

    @Builder
    public SiteResponse(String url) {
        this.url = url;
    }
}
