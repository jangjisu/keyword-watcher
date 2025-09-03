package com.app.keywordwatcher.web.service.keyword;

import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.keyword.KeywordRepository;
import com.app.keywordwatcher.web.controller.keyword.request.KeywordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeywordService {
    private final KeywordRepository keywordRepo;

    /**
     * 키워드 생성 (시스템 전체용)
     */
    @Transactional
    public Keyword createIfNonExistKeyword(KeywordRequest keywordRequest) {
        String normalizedKeyText = getNormalizeKeyText(keywordRequest);

        return keywordRepo.findByKeyText(normalizedKeyText)
                .orElseGet(() -> keywordRepo.save(Keyword.create(normalizedKeyText)));
    }

    public Keyword getKeyword(KeywordRequest keywordRequest) {
        String normalizedKeyText = getNormalizeKeyText(keywordRequest);

        return keywordRepo.findByKeyText(normalizedKeyText)
                .orElseThrow(() -> new IllegalArgumentException("키워드를 찾을 수 없습니다."));
    }

    private String getNormalizeKeyText(KeywordRequest keywordRequest) {
        return keywordRequest.getKeyText().trim().toLowerCase();
    }
}
