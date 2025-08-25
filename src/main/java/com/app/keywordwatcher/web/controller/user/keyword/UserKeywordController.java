package com.app.keywordwatcher.web.controller.user.keyword;

import com.app.keywordwatcher.web.controller.ApiResponse;
import com.app.keywordwatcher.web.controller.user.keyword.request.KeywordRequest;
import com.app.keywordwatcher.web.service.user.UserKeywordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/keyword")
public class UserKeywordController {
    private final UserKeywordService userKeywordService;

    @PostMapping("/add")
    public ApiResponse<Long> addKeyword(@Valid @RequestBody KeywordRequest keywordRequest, Authentication authentication) {
        return ApiResponse.success(userKeywordService.addKeyword(authentication.getName(), keywordRequest));
    }

    @PostMapping("/delete")
    public ApiResponse<Long> deleteKeyword(@Valid @RequestBody KeywordRequest keywordRequest, Authentication authentication) {
        return ApiResponse.success(userKeywordService.removeKeyword(authentication.getName(), keywordRequest));
    }
}
