package com.app.keywordwatcher.web.controller.user.site.request;

import com.app.keywordwatcher.domain.site.Site;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SiteRequest {
    @NotEmpty(message = "사이트 URL은 필수입니다.")
    private String url;

    @Builder
    public SiteRequest(String url) {
        this.url = url;
    }

    public Site toSite() {
        return Site.create(url);
    }
}
