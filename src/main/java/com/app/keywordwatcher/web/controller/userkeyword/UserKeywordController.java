package com.app.keywordwatcher.web.controller.userkeyword;

import com.app.keywordwatcher.web.controller.ApiResponse;
import com.app.keywordwatcher.web.controller.keyword.request.KeywordRequest;
import com.app.keywordwatcher.web.service.user.UserKeywordService;
import com.app.keywordwatcher.web.service.user.response.KeywordResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/keyword")
public class UserKeywordController {
    private final UserKeywordService userKeywordService;

    @GetMapping
    public ApiResponse<List<KeywordResponse>> getUserKeywords(Authentication authentication) {
        return ApiResponse.success(userKeywordService.getUserKeywords(authentication.getName()));
    }

    @PostMapping("/add")
    public ApiResponse<Long> addKeyword(@Valid @RequestBody KeywordRequest keywordRequest, Authentication authentication) {
        return ApiResponse.success(userKeywordService.addKeyword(authentication.getName(), keywordRequest));
    }

    @PostMapping("/delete")
    public ApiResponse<Long> deleteKeyword(@Valid @RequestBody KeywordRequest keywordRequest, Authentication authentication) {
        return ApiResponse.success(userKeywordService.removeKeyword(authentication.getName(), keywordRequest));
    }
}
