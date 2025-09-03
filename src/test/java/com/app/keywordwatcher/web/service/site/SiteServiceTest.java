package com.app.keywordwatcher.web.service.site;

import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.site.SiteRepository;
import com.app.keywordwatcher.web.controller.site.request.SiteRequest;
import com.app.keywordwatcher.web.service.ServiceTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class SiteServiceTest extends ServiceTestSupport {
    @Autowired
    private SiteService siteService;

    @Autowired
    private SiteRepository siteRepository;

    @DisplayName("새로운 사이트 생성 시 사이트가 등록된다")
    @Test
    void create_new_site() {
        // given
        SiteRequest siteRequest = SiteRequest.builder()
                .url("https://example.com")
                .build();

        // when
        Site site = siteService.createIfNonExistSite(siteRequest);

        // then
        assertThat(site.getUrl()).isEqualTo("https://example.com");

        List<Site> sites = siteRepository.findAll();
        assertThat(sites).hasSize(1)
                .extracting("url")
                .containsExactly("https://example.com");
    }

    @DisplayName("이미 존재하는 사이트 생성 시 기존 사이트를 반환한다")
    @Test
    void create_exist_site() {
        // given
        Site existSite = Site.create("https://example.com");
        siteRepository.save(existSite);

        SiteRequest siteRequest = SiteRequest.builder()
                .url("https://example.com")
                .build();

        // when
        Site site = siteService.createIfNonExistSite(siteRequest);

        // then
        assertThat(site.getId()).isEqualTo(existSite.getId());
        assertThat(site.getUrl()).isEqualTo("https://example.com");

        List<Site> sites = siteRepository.findAll();
        assertThat(sites).hasSize(1);
    }

    @DisplayName("존재하는 사이트를 조회할 수 있다")
    @Test
    void get_exist_site() {
        // given
        Site existSite = Site.create("https://example.com");
        siteRepository.save(existSite);

        SiteRequest siteRequest = SiteRequest.builder()
                .url("https://example.com")
                .build();

        // when
        Site site = siteService.getSite(siteRequest);

        // then
        assertThat(site.getId()).isEqualTo(existSite.getId());
        assertThat(site.getUrl()).isEqualTo("https://example.com");
    }

    @DisplayName("존재하지 않는 사이트를 조회하면 예외가 발생한다")
    @Test
    void get_non_exist_site() {
        // given
        SiteRequest siteRequest = SiteRequest.builder()
                .url("https://nonexist.com")
                .build();

        // when // then
        assertThatThrownBy(() -> siteService.getSite(siteRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사이트를 찾을 수 없습니다.");
    }

    @DisplayName("사이트는 정규화 없이 사용자가 입력한 그대로 저장된다")
    @Test
    void site_stored_as_user_input() {
        // given
        SiteRequest siteRequest1 = SiteRequest.builder()
                .url("HTTPS://EXAMPLE.COM")
                .build();

        SiteRequest siteRequest2 = SiteRequest.builder()
                .url("https://example.com")
                .build();

        // when
        Site site1 = siteService.createIfNonExistSite(siteRequest1);
        Site site2 = siteService.createIfNonExistSite(siteRequest2);

        // then
        assertThat(site1.getUrl()).isEqualTo("HTTPS://EXAMPLE.COM");
        assertThat(site2.getUrl()).isEqualTo("https://example.com");

        List<Site> sites = siteRepository.findAll();
        assertThat(sites).hasSize(2)
                .extracting("url")
                .containsExactlyInAnyOrder("HTTPS://EXAMPLE.COM", "https://example.com");
    }

    @DisplayName("유효한 URL을 테스트하면 true를 반환한다")
    @Test
    void testUrl_returns_true_for_valid_url() {
        // given
        SiteRequest siteRequest = SiteRequest.builder()
                .url("https://culture.seoul.go.kr/culture/bbs/B0000002/list.do?menuNo=200052")
                .build();

        // when
        boolean result = siteService.testUrl(siteRequest);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("잘못된 URL을 테스트하면 false를 반환한다")
    @Test
    void testUrl_returns_false_for_invalid_url() {
        // given
        SiteRequest siteRequest = SiteRequest.builder()
                .url("https://invalid-url-that-does-not-exist.com/nonexistent-page")
                .build();

        // when
        boolean result = siteService.testUrl(siteRequest);

        // then
        assertThat(result).isFalse();
    }
}
