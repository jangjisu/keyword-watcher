package com.app.keywordwatcher.domain.site;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SiteRepositoryTest {
    @Autowired
    private SiteRepository siteRepository;

    @DisplayName("사이트가 정상적으로 등록되는지 확인한다.")
    @Test
    void siteAddTest() {
        // given
        Site site = Site.create("https://example.com", 1, 3);

        // when
        Site save = siteRepository.save(site);

        Optional<Site> savedSite = siteRepository.findById(save.getId());

        // then
        assertThat(savedSite).contains(save);

    }
}