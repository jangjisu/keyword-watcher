package com.app.keywordwatcher.web.controller.user.site.request;

import com.app.keywordwatcher.domain.site.Site;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SiteRequest {
    @NotEmpty(message = "사이트 URL은 필수입니다.")
    private String url;

    @NotNull(message = "제목 index는 필수입니다.")
    private Integer titleIndex;

    @NotNull(message = "작성일 index는 필수입니다.")
    private Integer createAtIndex;

    @Builder
    public SiteRequest(String url, Integer titleIndex, Integer createAtIndex) {
        this.url = url;
        this.titleIndex = titleIndex;
        this.createAtIndex = createAtIndex;
    }

    public Site toSite() {
        return Site.create(url, titleIndex, createAtIndex);
    }
}
