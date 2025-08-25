package com.app.keywordwatcher.web.service.keyword;

import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.keyword.KeywordRepository;
import com.app.keywordwatcher.web.controller.user.keyword.request.KeywordRequest;
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
        String norm = normalize(keywordRequest.getKeyText());

        return keywordRepo.findByKeyText(norm)
                .orElseGet(() -> keywordRepo.save(keywordRequest.toKeyword()));
    }

    public Keyword getKeyword(KeywordRequest keywordRequest) {
        String norm = normalize(keywordRequest.getKeyText());
        return keywordRepo.findByKeyText(norm)
                .orElseThrow(() -> new IllegalArgumentException("키워드를 찾을 수 없습니다."));
    }

    private String normalize(String keyText) {
        return keyText.trim().toLowerCase();
    }
}
