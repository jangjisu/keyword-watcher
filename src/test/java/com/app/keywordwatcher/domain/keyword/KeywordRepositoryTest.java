package com.app.keywordwatcher.domain.keyword;

import com.app.keywordwatcher.domain.RepositoryTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class KeywordRepositoryTest extends RepositoryTestSupport {
    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        Keyword keyword = Keyword.create("서울");
        keywordRepository.save(keyword);
        entityManager.flush();
    }

    @DisplayName("키워드가 존재하는 경우 조회된다")
    @Test
    void keyword_exist() {
        // given // when
        Optional<Keyword> exists = keywordRepository.findByKeyText("서울");

        // then
        assertThat(exists).isPresent().get()
                .extracting(Keyword::getKeyText)
                .isEqualTo("서울");

    }

    @DisplayName("키워드가 존재하지 않는 경우 빈 Optional을 반환한다")
    @Test
    void keyword_non_exists() {
        // given // when
        Optional<Keyword> nonExists = keywordRepository.findByKeyText("부산");

        // then
        assertThat(nonExists).isNotPresent();
    }
}