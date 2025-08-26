package com.app.keywordwatcher.web.service.user;

import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.user.User;
import com.app.keywordwatcher.domain.usersite.UserSite;
import com.app.keywordwatcher.web.controller.user.site.request.SiteRequest;
import com.app.keywordwatcher.web.service.site.SiteService;
import com.app.keywordwatcher.web.service.user.response.SiteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserSiteService {
    private final SiteService siteService;
    private final UserService userService;

    /**
     * 사용자의 사이트 목록 조회
     */
    public List<SiteResponse> getUserSites(String userId) {
        return getUser(userId).getUserSites().stream()
                .map(UserSite::getSite)
                .map(Site::toResponse)
                .toList();
    }

    /**
     * 사용자에게 사이트 추가
     */
    @Transactional
    public Long addSite(String userId, SiteRequest siteRequest) {
        Site site = siteService.createIfNonExistSite(siteRequest);
        getUser(userId).addUserSite(site);
        return site.getId();
    }

    /**
     * URL로 사용자에게서 사이트 제거
     */
    @Transactional
    public Long removeSite(String userId, SiteRequest siteRequest) {
        Site site = siteService.getSite(siteRequest);
        getUser(userId).removeUserSite(site);
        return site.getId();
    }

    private User getUser(String userId) {
        return userService.findByUserId(userId);
    }
}
