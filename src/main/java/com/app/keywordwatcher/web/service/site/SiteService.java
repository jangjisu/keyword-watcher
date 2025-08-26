package com.app.keywordwatcher.web.service.site;

import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.site.SiteRepository;
import com.app.keywordwatcher.web.controller.user.site.request.SiteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteService {
    private final SiteRepository siteRepository;

    /**
     * 사이트 생성 (시스템 전체용) - 사용자가 입력한 URL 그대로 저장
     */
    @Transactional
    public Site createIfNonExistSite(SiteRequest siteRequest) {
        return siteRepository.findByUrl(siteRequest.getUrl())
                .orElseGet(() -> siteRepository.save(siteRequest.toSite()));
    }

    public Site getSite(SiteRequest siteRequest) {
        return siteRepository.findByUrl(siteRequest.getUrl())
                .orElseThrow(() -> new IllegalArgumentException("사이트를 찾을 수 없습니다."));
    }
}
