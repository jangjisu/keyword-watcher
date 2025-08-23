package com.app.keywordwatcher.web.service.user;

import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.keyword.KeywordRepository;
import com.app.keywordwatcher.domain.user.User;
import com.app.keywordwatcher.domain.user.UserRepository;
import com.app.keywordwatcher.web.controller.keyword.request.KeywordRequest;
import com.app.keywordwatcher.web.service.ServiceTestSupport;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class UserKeywordServiceTest extends ServiceTestSupport {
    @Autowired
    private UserKeywordService userKeywordService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;


    @BeforeEach
    void setUp() {
        User user = User.create("existUser", "exist@example.com", "password123", passwordEncoder);
        userRepository.save(user);

        User user2 = User.create("existUser2", "exist2@example.com", "password123", passwordEncoder);
        userRepository.save(user2);

        Keyword keyword = Keyword.create("서울");
        keywordRepository.save(keyword);

        Keyword keyword1 = Keyword.create("부산");
        keywordRepository.save(keyword1);

        entityManager.flush();

        user.addUserKeyword(keyword);
        user.addUserKeyword(keyword1);

        entityManager.flush();
    }

    @DisplayName("존재하는 유저의 키워드를 조회할 수 있다")
    @Test
    void get_user_keywords() {
        // given
        List<Keyword> keywords = userKeywordService.getUserKeywords("existUser");

        // when // then
        assertThat(keywords).hasSize(2)
                .extracting("keyText")
                .containsExactlyInAnyOrder("서울", "부산");
    }

    @DisplayName("존재하지 않는 유저의 키워드를 조회하면 에러가 발생한다")
    @Test
    void get_non_exist_user_keywords() {
        // given // when // then
        assertThatThrownBy(() -> userKeywordService.getUserKeywords("nonExistUser"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("이미 등록된 키워드를 추가할 경우, 유저 키워드가 등록된다.")
    @Test
    void add_exist_keyword() {
        // given
        KeywordRequest existKeyword = KeywordRequest.builder()
                .keyText("서울")
                .build();

        // when
        userKeywordService.addKeyword("existUser2", existKeyword);

        // then
        List<Keyword> keywords = keywordRepository.findAll();
        assertThat(keywords).hasSize(2)
                .extracting("keyText")
                .containsExactlyInAnyOrder("서울", "부산");

        Optional<User> user = userRepository.findByUserId("existUser2");
        assertThat(user).isPresent();

        assertThat(user.get().getUserKeywords()).hasSize(1)
                .extracting(it -> it.getKeyword().getKeyText())
                .containsExactlyInAnyOrder("서울");
    }

    @DisplayName("등록되지 않은 키워드를 추가할 경우, 키워드 테이블에 값을 추가 한 뒤, 유저에 키워드를 등록한다.")
    @Test
    void add_non_exist_keyword() {
        // given
        KeywordRequest newKeyword = KeywordRequest.builder()
                .keyText("강원")
                .build();

        // when
        userKeywordService.addKeyword("existUser", newKeyword);

        // then
        List<Keyword> keywords = keywordRepository.findAll();
        assertThat(keywords).hasSize(3)
                .extracting("keyText")
                .containsExactlyInAnyOrder("서울", "부산", "강원");

        Optional<User> user = userRepository.findByUserId("existUser");
        assertThat(user).isPresent();

        assertThat(user.get().getUserKeywords()).hasSize(3)
                .extracting(it -> it.getKeyword().getKeyText())
                .containsExactlyInAnyOrder("서울", "부산", "강원");
    }

    @DisplayName("유저에 등록된 키워드를 삭제할 수 있다.")
    @Test
    void delete_exist_keyword() {
        // given
        KeywordRequest existKeyword = KeywordRequest.builder()
                .keyText("서울")
                .build();

        // when
        userKeywordService.removeKeyword("existUser", existKeyword);

        // then
        Optional<User> user = userRepository.findByUserId("existUser");
        assertThat(user).isPresent();

        assertThat(user.get().getUserKeywords()).hasSize(1)
                .extracting(it -> it.getKeyword().getKeyText())
                .containsExactlyInAnyOrder("부산");
    }

    @DisplayName("등록되지 않은 키워드를 삭제 요청할 경우 에러가 발생한다.")
    @Test
    void delete_non_exist_keyword() {
        // given
        KeywordRequest nonExistKeyword = KeywordRequest.builder()
                .keyText("주차")
                .build();

        // when // then
        assertThatThrownBy(() -> userKeywordService.removeKeyword("existUser", nonExistKeyword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("키워드를 찾을 수 없습니다.");
    }
}