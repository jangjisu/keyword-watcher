package com.app.keywordwatcher.web.service.keyword;

import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.keyword.KeywordRepository;
import com.app.keywordwatcher.web.controller.keyword.request.KeywordRequest;
import com.app.keywordwatcher.web.service.ServiceTestSupport;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class KeywordServiceTest extends ServiceTestSupport {
    @Autowired
    private KeywordService keywordService;

    @Autowired
    private KeywordRepository keywordRepository;

    @BeforeEach
    void setUp() {
        keywordRepository.save(Keyword.create("test"));
    }

    @DisplayName("존재하는 키워드를 조회한다")
    @Test
    void get_exist_keyword() {
        // given
        KeywordRequest keywordRequest = KeywordRequest.builder()
                .keyText("test")
                .build();

        // when
        Keyword keyword = keywordService.getKeyword(keywordRequest);

        // then
        assertThat(keyword).extracting("keyText").isEqualTo("test");
    }

    @DisplayName("대소문자가 구분없이 존재하는 키워드를 조회한다")
    @Test
    void get_exist_upper_case_keyword() {
        // given
        KeywordRequest keywordRequest = KeywordRequest.builder()
                .keyText("TEST")
                .build();

        // when
        Keyword keyword = keywordService.getKeyword(keywordRequest);

        // then
        assertThat(keyword).extracting("keyText").isEqualTo("test");
    }

    @DisplayName("존재하지 않는 키워드를 조회하면 예외가 발생한다")
    @Test
    void get_not_exist_keyword() {
        // given
        KeywordRequest keywordRequest = KeywordRequest.builder()
                .keyText("nonexistent")
                .build();

        // when // then
        assertThatThrownBy(() -> keywordService.getKeyword(keywordRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("키워드를 찾을 수 없습니다.");
    }

    @DisplayName("존재하지 않는 키워드는 새로 생성한다")
    @Test
    void create_if_non_exist_keyword() {
        // given
        KeywordRequest keywordRequest = KeywordRequest.builder()
                .keyText("new")
                .build();

        // when
        Keyword keyword = keywordService.createIfNonExistKeyword(keywordRequest);

        // then
        assertThat(keyword).extracting("keyText").isEqualTo("new");

        List<Keyword> keywords = keywordRepository.findAll();
        assertThat(keywords).hasSize(2)
                .extracting("keyText")
                .containsExactlyInAnyOrder("test", "new");
    }

    @DisplayName("이미 존재하는 키워드는 새로 생성하지 않고 기존 키워드를 반환한다")
    @Test
    void create_if_exist_keyword() {
        // given
        KeywordRequest keywordRequest = KeywordRequest.builder()
                .keyText("test")
                .build();

        // when
        Keyword keyword = keywordService.createIfNonExistKeyword(keywordRequest);

        // then
        assertThat(keyword).extracting("keyText").isEqualTo("test");

        List<Keyword> keywords = keywordRepository.findAll();
        assertThat(keywords).hasSize(1)
                .extracting("keyText")
                .containsExactly("test");
    }
}