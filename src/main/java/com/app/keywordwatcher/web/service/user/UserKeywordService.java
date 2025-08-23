package com.app.keywordwatcher.web.service.user;

import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.user.User;
import com.app.keywordwatcher.domain.userkeyword.UserKeyword;
import com.app.keywordwatcher.web.controller.keyword.request.KeywordRequest;
import com.app.keywordwatcher.web.service.keyword.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserKeywordService {
    private final KeywordService keywordService;
    private final UserService userService;

    /**
     * 사용자의 키워드 목록 조회
     */
    public List<Keyword> getUserKeywords(String userId) {
        return getUser(userId).getUserKeywords().stream()
                .map(UserKeyword::getKeyword)
                .toList();
    }

    /**
     * 사용자에게 키워드 추가
     */
    @Transactional
    public void addKeyword(String userId, KeywordRequest keywordRequest) {
        Keyword keyword = keywordService.createIfNonExistKeyword(keywordRequest);
        getUser(userId).addUserKeyword(keyword);
    }

    /**
     * keyText로 사용자에게서 키워드 제거
     */
    @Transactional
    public void removeKeyword(String userId, KeywordRequest keywordRequest) {
        Keyword keyword = keywordService.getKeyword(keywordRequest);
        getUser(userId).removeUserKeyword(keyword);
    }


    private User getUser(String userId) {
        return userService.findByUserId(userId);
    }
}
