package com.app.keywordwatcher.domain.site;

import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.keyword.KeywordRepository;
import com.app.keywordwatcher.domain.sitekeyword.SiteKeyword;
import com.app.keywordwatcher.domain.sitekeyword.SiteKeywordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class SiteRepositoryTest {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private SiteKeywordRepository siteKeywordRepository;

    @DisplayName("Site 저장 시 SiteKeyword도 함께 저장된다")
    @Test
    void save_site_with_keywords() {
        // given
        Site site = Site.create("https://example.com", 1, 2);

        Keyword keyword1 = keywordRepository.save(Keyword.create("서울"));
        Keyword keyword2 = keywordRepository.save(Keyword.create("부산"));

        site.addSiteKeyword(keyword1);
        site.addSiteKeyword(keyword2);

        // when
        Site savedSite = siteRepository.save(site);

        // then
        List<SiteKeyword> siteKeywords = siteKeywordRepository.findBySiteId(savedSite.getId());
        assertThat(siteKeywords).hasSize(2)
            .extracting(sk -> sk.getKeyword().getKeyText())
            .containsExactlyInAnyOrder("서울", "부산");
    }

    @DisplayName("Site가 삭제되면 연관된 SiteKeyword도 함께 삭제된다")
    @Test
    void delete_site_with_keywords() {
        // given
        Site site = Site.create("https://example.com", 1, 2);
        Keyword keyword = keywordRepository.save(Keyword.create("대구"));
        site.addSiteKeyword(keyword);

        Site savedSite = siteRepository.save(site);
        Long siteId = savedSite.getId();

        // when
        siteRepository.delete(savedSite);

        // then
        assertThat(siteRepository.findById(siteId)).isEmpty();
        assertThat(siteKeywordRepository.findBySiteId(siteId)).isEmpty();
    }

    @DisplayName("Site를 저장할 때 바뀐 SiteKeyword 목록이 올바르게 반영된다")
    @Test
    void update_site_keywords() {
        // given
        Site site = Site.create("https://example.com", 1, 2);
        Keyword keyword1 = keywordRepository.save(Keyword.create("서울"));
        Keyword keyword2 = keywordRepository.save(Keyword.create("부산"));
        Keyword keyword3 = keywordRepository.save(Keyword.create("대구"));

        site.addSiteKeyword(keyword1);
        site.addSiteKeyword(keyword2);
        Site savedSite = siteRepository.save(site);

        // when
        savedSite.removeSiteKeyword(keyword1);
        savedSite.addSiteKeyword(keyword3);
        siteRepository.save(savedSite);

        // then
        List<SiteKeyword> updatedKeywords = siteKeywordRepository.findBySiteId(savedSite.getId());
        assertThat(updatedKeywords).hasSize(2);
        assertThat(updatedKeywords)
            .extracting(sk -> sk.getKeyword().getKeyText())
            .containsExactlyInAnyOrder("부산", "대구");
    }

    @DisplayName("replaceSiteKeywords로 SiteKeyword목록을 교체할 수 있다")
    @Test
    void replace_site_keywords_persisted() {
        // given
        Site site = Site.create("https://example.com", 1, 2);
        Keyword keyword1 = keywordRepository.save(Keyword.create("서울"));
        Keyword keyword2 = keywordRepository.save(Keyword.create("부산"));
        Keyword keyword3 = keywordRepository.save(Keyword.create("대구"));
        Keyword keyword4 = keywordRepository.save(Keyword.create("인천"));

        site.addSiteKeyword(keyword1);
        site.addSiteKeyword(keyword2);
        Site savedSite = siteRepository.save(site);

        // when
        List<Keyword> newKeywords = List.of(keyword1, keyword3, keyword4);
        savedSite.replaceSiteKeywords(newKeywords);
        siteRepository.save(savedSite);

        // then - DB에 올바르게 반영되었는지 확인
        List<SiteKeyword> result = siteKeywordRepository.findBySiteId(savedSite.getId());
        assertThat(result).hasSize(3);
        assertThat(result)
            .extracting(sk -> sk.getKeyword().getKeyText())
            .containsExactlyInAnyOrder("서울", "대구", "인천");
    }
}
