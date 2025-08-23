package com.app.keywordwatcher.web.controller.keyword.request;

import com.app.keywordwatcher.domain.keyword.Keyword;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class KeywordRequest {
    @NotEmpty(message = "키워드는 필수입니다.")
    private String keyText;

    @Builder
    public KeywordRequest(String keyText) {
        this.keyText = keyText;
    }

    public Keyword toKeyword() {
        return Keyword.create(keyText);
    }
}
