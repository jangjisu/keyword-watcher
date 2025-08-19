package com.app.keywordwatcher.domain.user;

import com.app.keywordwatcher.domain.RepositoryTestSupport;
import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.keyword.KeywordRepository;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.site.SiteRepository;
import com.app.keywordwatcher.domain.userkeyword.UserKeyword;
import com.app.keywordwatcher.domain.userkeyword.UserKeywordRepository;
import com.app.keywordwatcher.domain.usersite.UserSite;
import com.app.keywordwatcher.domain.usersite.UserSiteRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class UserRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private UserSiteRepository userSiteRepository;

    @Autowired
    private UserKeywordRepository userKeywordRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        User user = User.create("testuser", "test@example.com", "password123", passwordEncoder);
        userRepository.save(user);
        entityManager.flush();
    }

    @DisplayName("사용자 ID로 사용자를 찾을 수 있다")
    @Test
    void findByUserId() {
        // given // when
        Optional<User> foundUser = userRepository.findByUserId("testuser");

        // then
        assertThat(foundUser).isPresent();

        assertThat(foundUser.get())
                .extracting("userId", "email")
                .containsExactly("testuser", "test@example.com");
    }

    @DisplayName("존재하지 않는 사용자 ID로 조회 시 빈 Optional을 반환한다")
    @Test
    void findByUserId_notFound() {
        // when
        Optional<User> foundUser = userRepository.findByUserId("nonexistent");

        // then
        assertThat(foundUser).isEmpty();
    }

    @DisplayName("사용자 ID 존재 여부를 확인할 수 있다")
    @Test
    void existsByUserId() {
        // given // when // then
        assertThat(userRepository.existsByUserId("testuser")).isTrue();
        assertThat(userRepository.existsByUserId("nonexistent")).isFalse();
    }

    @DisplayName("이메일 존재 여부를 확인할 수 있다")
    @Test
    void existsByEmail() {
        // given // when // then
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @DisplayName("User 저장 시 UserSite도 함께 저장된다")
    @Test
    void save_user_with_sites() {
        // given
        User user2 = User.create("testuser2", "test2@example.com", "password123", passwordEncoder);
        Site site = siteRepository.save(Site.create("https://example.com", 1, 2));
        Site site2 = siteRepository.save(Site.create("https://example2.com", 1, 2));

        user2.addUserSite(site);
        user2.addUserSite(site2);

        // when
        User savedUser = userRepository.save(user2);

        // then
        List<UserSite> userSites = userSiteRepository.findByUserId(savedUser.getId());
        assertThat(userSites).hasSize(2)
                .extracting(sk -> sk.getSite().getUrl())
                .containsExactlyInAnyOrder("https://example.com", "https://example2.com");
    }

    @DisplayName("User 삭제되면 연관된 UserSite도 함께 삭제된다")
    @Test
    void delete_user_with_sites() {
        // given
        User user2 = User.create("testuser2", "test2@example.com", "password123", passwordEncoder);
        Site savedSite = siteRepository.save(Site.create("https://example.com", 1, 2));
        user2.addUserSite(savedSite);

        User savedUser = userRepository.save(user2);
        Long userId = savedUser.getId();


        // when
        userRepository.delete(savedUser);

        // then
        assertThat(userRepository.findById(userId)).isEmpty();
        assertThat(userSiteRepository.findByUserId(userId)).isEmpty();
    }

    @DisplayName("User를 저장할 때 바뀐 UserSite 목록이 올바르게 반영된다")
    @Test
    void update_user_sites() {
        // given
        User user2 = User.create("testuser2", "test2@example.com", "password123", passwordEncoder);
        Site site = siteRepository.save(Site.create("https://example.com", 1, 2));
        Site site2 = siteRepository.save(Site.create("https://example2.com", 1, 2));
        Site site3 = siteRepository.save(Site.create("https://example3.com", 1, 2));

        user2.addUserSite(site);
        user2.addUserSite(site3);

        User savedUser = userRepository.save(user2);
        entityManager.flush();

        // when
        User reloadedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        reloadedUser.removeUserSite(site);
        reloadedUser.addUserSite(site2);
        userRepository.save(reloadedUser);

        // then
        List<UserSite> updatedSites = userSiteRepository.findByUserId(savedUser.getId());
        assertThat(updatedSites).hasSize(2);
        assertThat(updatedSites)
                .extracting(sk -> sk.getSite().getUrl())
                .containsExactlyInAnyOrder("https://example2.com", "https://example3.com");
    }

    @DisplayName("User 저장 시 UserKeyword도 함께 저장된다")
    @Test
    void save_user_with_keywords() {
        // given
        User user2 = User.create("testuser2", "test2@example.com", "password123", passwordEncoder);
        Keyword keyword1 = keywordRepository.save(Keyword.create("키워드1"));
        Keyword keyword2 = keywordRepository.save(Keyword.create("키워드2"));

        user2.addUserKeyword(keyword1);
        user2.addUserKeyword(keyword2);

        // when
        User savedUser = userRepository.save(user2);

        // then
        List<UserKeyword> userKeywords = userKeywordRepository.findByUserId(savedUser.getId());
        assertThat(userKeywords).hasSize(2)
                .extracting(uk -> uk.getKeyword().getKeyText())
                .containsExactlyInAnyOrder("키워드1", "키워드2");
    }

    @DisplayName("User 삭제되면 연관된 UserKeyword도 함께 삭제된다")
    @Test
    void delete_user_with_keywords() {
        // given
        User user2 = User.create("testuser2", "test2@example.com", "password123", passwordEncoder);
        Keyword keyword = keywordRepository.save(Keyword.create("키워드1"));
        user2.addUserKeyword(keyword);

        User savedUser = userRepository.save(user2);
        Long userId = savedUser.getId();

        // when
        userRepository.delete(savedUser);

        // then
        assertThat(userRepository.findById(userId)).isEmpty();
        assertThat(userKeywordRepository.findByUserId(userId)).isEmpty();
    }

    @DisplayName("User를 저장할 때 바뀐 UserKeyword 목록이 올바르게 반영된다")
    @Test
    void update_user_keywords() {
        // given
        User user2 = User.create("testuser2", "test2@example.com", "password123", passwordEncoder);
        Keyword keyword1 = keywordRepository.save(Keyword.create("키워드1"));
        Keyword keyword2 = keywordRepository.save(Keyword.create("키워드2"));
        Keyword keyword3 = keywordRepository.save(Keyword.create("키워드3"));

        user2.addUserKeyword(keyword1);
        user2.addUserKeyword(keyword3);

        User savedUser = userRepository.save(user2);
        entityManager.flush();

        // when
        User reloadedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        reloadedUser.removeUserKeyword(keyword1);
        reloadedUser.addUserKeyword(keyword2);
        userRepository.save(reloadedUser);

        // then
        List<UserKeyword> updatedKeywords = userKeywordRepository.findByUserId(savedUser.getId());
        assertThat(updatedKeywords).hasSize(2);
        assertThat(updatedKeywords)
                .extracting(uk -> uk.getKeyword().getKeyText())
                .containsExactlyInAnyOrder("키워드2", "키워드3");
    }
}
