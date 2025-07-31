package com.app.keywordwatcher.domain.sitekeyword;

import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.keyword.KeywordRepository;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.site.SiteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class SiteKeywordRepositoryTest {

    @Autowired
    private SiteKeywordRepository siteKeywordRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @DisplayName("findBySiteId로 특정 사이트의 키워드들을 조회할 수 있다")
    @Test
    void findBySiteId() {
        // given
        Site site1 = siteRepository.save(Site.create("https://site1.com", 1, 2));
        Site site2 = siteRepository.save(Site.create("https://site2.com", 1, 2));

        Keyword keyword1 = keywordRepository.save(Keyword.create("서울"));
        Keyword keyword2 = keywordRepository.save(Keyword.create("부산"));
        Keyword keyword3 = keywordRepository.save(Keyword.create("대구"));

        siteKeywordRepository.save(SiteKeyword.create(site1, keyword1));
        siteKeywordRepository.save(SiteKeyword.create(site1, keyword2));
        siteKeywordRepository.save(SiteKeyword.create(site2, keyword3));

        // when
        List<SiteKeyword> site1Keywords = siteKeywordRepository.findBySiteId(site1.getId());
        List<SiteKeyword> site2Keywords = siteKeywordRepository.findBySiteId(site2.getId());

        // then
        assertThat(site1Keywords).hasSize(2)
            .extracting(sk -> sk.getKeyword().getKeyText())
            .containsExactlyInAnyOrder("서울", "부산");

        assertThat(site2Keywords).hasSize(1)
            .extracting(sk -> sk.getKeyword().getKeyText())
            .containsExactly("대구");
    }
}