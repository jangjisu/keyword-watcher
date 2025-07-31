package com.app.keywordwatcher.domain.site;

import com.app.keywordwatcher.domain.keyword.Keyword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SiteTest {

    @DisplayName("Keyword 객체를 통해 SiteKeyword를 추가할 수 있다")
    @Test
    void addSiteKeyword_with_keyword() {
        // given
        Site site = Site.create("https://example.com", 1, 2);
        Keyword keyword = Keyword.create("테스트키워드");

        // when
        site.addSiteKeyword(keyword);

        // then
        assertThat(site.getSiteKeywords()).hasSize(1);
        assertThat(site.getSiteKeywords().get(0).getKeyword()).isEqualTo(keyword);
        assertThat(site.getSiteKeywords().get(0).getSite()).isEqualTo(site);
    }


    @DisplayName("Keyword 객체를 통해 SiteKeyword를 삭제할 수 있다")
    @Test
    void removeSiteKeyword_with_keyword() {
        // given
        Site site = Site.create("https://example.com", 1, 2);
        Keyword keyword1 = Keyword.create("키워드1");
        Keyword keyword2 = Keyword.create("키워드2");

        site.addSiteKeyword(keyword1);
        site.addSiteKeyword(keyword2);

        assertThat(site.getSiteKeywords()).hasSize(2);

        // when
        site.removeSiteKeyword(keyword1);

        // then
        assertThat(site.getSiteKeywords()).hasSize(1);
        assertThat(site.getSiteKeywords().get(0).getKeyword()).isEqualTo(keyword2);
    }

    @DisplayName("여러 개의 키워드를 추가하고 삭제할 수 있다")
    @Test
    void multiple_keywords_operations() {
        // given
        Site site = Site.create("https://example.com", 1, 2);
        Keyword keyword1 = Keyword.create("서울");
        Keyword keyword2 = Keyword.create("부산");
        Keyword keyword3 = Keyword.create("대구");

        // when - 키워드들 추가
        site.addSiteKeyword(keyword1);
        site.addSiteKeyword(keyword2);
        site.addSiteKeyword(keyword3);

        // then
        assertThat(site.getSiteKeywords()).hasSize(3);

        // when - 하나 삭제
        site.removeSiteKeyword(keyword2);

        // then
        assertThat(site.getSiteKeywords()).hasSize(2);
        assertThat(site.getSiteKeywords())
                .extracting(siteKeyword -> siteKeyword.getKeyword().getKeyText())
                .containsExactlyInAnyOrder("서울", "대구");
    }
}
