package com.app.keywordwatcher.domain.userkeyword;

import com.app.keywordwatcher.domain.RepositoryTestSupport;
import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.keyword.KeywordRepository;
import com.app.keywordwatcher.domain.user.User;
import com.app.keywordwatcher.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class UserKeywordRepositoryTest extends RepositoryTestSupport {
    @Autowired
    private UserKeywordRepository userKeywordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @DisplayName("findByUserId로 특정 유저가 사용하는 키워드들을 조회할 수 있다")
    @Test
    void findByUserId() {
        // given
        User user1 = userRepository.save(User.create("testuser", "test@example.com", "password123", passwordEncoder));
        User user2 = userRepository.save(User.create("testuser2", "test2@example.com", "password123", passwordEncoder));

        Keyword keyword1 = keywordRepository.save(Keyword.create("서울"));
        Keyword keyword2 = keywordRepository.save(Keyword.create("부산"));
        Keyword keyword3 = keywordRepository.save(Keyword.create("대구"));

        userKeywordRepository.save(UserKeyword.create(user1, keyword1));
        userKeywordRepository.save(UserKeyword.create(user1, keyword2));
        userKeywordRepository.save(UserKeyword.create(user2, keyword3));

        // when
        List<UserKeyword> user1Keywords = userKeywordRepository.findByUserId(user1.getId());
        List<UserKeyword> user2Keywords = userKeywordRepository.findByUserId(user2.getId());

        // then
        assertThat(user1Keywords).hasSize(2)
                .extracting(sk -> sk.getKeyword().getKeyText())
                .containsExactlyInAnyOrder("서울", "부산");

        assertThat(user2Keywords).hasSize(1)
                .extracting(sk -> sk.getKeyword().getKeyText())
                .containsExactly("대구");
    }
}