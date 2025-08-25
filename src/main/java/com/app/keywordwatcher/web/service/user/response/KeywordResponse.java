package com.app.keywordwatcher.web.service.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KeywordResponse {
    private String keyText;

    @Builder
    public KeywordResponse(String keyText) {
        this.keyText = keyText;
    }
}
