package com.app.keywordwatcher.web.service.site;

import com.app.keywordwatcher.crawler.CrawlingHandler;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.site.SiteRepository;
import com.app.keywordwatcher.web.controller.site.request.SiteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteService {
    private final SiteRepository siteRepository;
    private final CrawlingHandler crawlingHandlerChain;

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

    /**
     * URL 접근 가능 여부 테스트 (간단한 연결 + title 추출)
     */
    public boolean testUrl(SiteRequest request) {
        return crawlingHandlerChain.canHandle(request.getUrl());
    }
}
