package com.app.keywordwatcher.web.service.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SiteResponse {
    private String url;
    private Integer titleIndex;
    private Integer createAtIndex;

    @Builder
    public SiteResponse(String url, Integer titleIndex, Integer createAtIndex) {
        this.url = url;
        this.titleIndex = titleIndex;
        this.createAtIndex = createAtIndex;
    }
}
