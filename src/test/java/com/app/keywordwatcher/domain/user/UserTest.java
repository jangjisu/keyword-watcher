package com.app.keywordwatcher.domain.user;

import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.site.Site;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @DisplayName("User 객체를 통해 Site를 추가할 수 있다")
    @Test
    void add_site_from_user() {
        // given
        User user = User.create("testuser", "test@example.com", "password123", passwordEncoder);
        Site site = Site.create("https://example.com");

        //when
        user.addUserSite(site);

        // then
        assertThat(user.getUserSites()).hasSize(1)
                .first()
                .satisfies(userSite -> {
                    assertThat(userSite.getUser()).isEqualTo(user);
                    assertThat(userSite.getSite()).isEqualTo(site);
                });
    }

    @DisplayName("User 객체를 통해 Site를 중복 추가할 경우 개수는 변하지 않는다")
    @Test
    void add_duplicate_site() {
        // given
        User user = User.create("testuser", "test@example.com", "password123", passwordEncoder);
        Site site = Site.create("https://example.com");

        //when
        user.addUserSite(site);
        user.addUserSite(site);

        // then
        assertThat(user.getUserSites()).hasSize(1)
                .first()
                .satisfies(userSite -> {
                    assertThat(userSite.getUser()).isEqualTo(user);
                    assertThat(userSite.getSite()).isEqualTo(site);
                });
    }


    @DisplayName("User 객체를 통해 Site를 삭제할 수 있다")
    @Test
    void remove_site_from_user() {
        // given
        User user = User.create("testuser", "test@example.com", "password123", passwordEncoder);
        Site site = Site.create("https://example.com");
        Site site2 = Site.create("https://exampletwo.com");

        user.addUserSite(site);
        user.addUserSite(site2);

        assertThat(user.getUserSites()).hasSize(2)
                .satisfiesExactly(
                        userSite -> {
                            assertThat(userSite.getUser()).isEqualTo(user);
                            assertThat(userSite.getSite()).isEqualTo(site);
                        },
                        userSite -> {
                            assertThat(userSite.getUser()).isEqualTo(user);
                            assertThat(userSite.getSite()).isEqualTo(site2);
                        }
                );

        // when
        user.removeUserSite(site);

        // then
        assertThat(user.getUserSites()).hasSize(1)
                .first()
                .satisfies(userSite -> {
                    assertThat(userSite.getUser()).isEqualTo(user);
                    assertThat(userSite.getSite()).isEqualTo(site2);
                });
    }

    @DisplayName("User 객체를 통해 Keyword를 추가할 수 있다")
    @Test
    void addKeyword_from_User() {
        // given
        User user = User.create("testuser", "test@example.com", "password123", passwordEncoder);
        Keyword keyword = Keyword.create("테스트키워드");

        //when
        user.addUserKeyword(keyword);

        // then
        assertThat(user.getUserKeywords()).hasSize(1)
                .first()
                .satisfies(userKeyword -> {
                    assertThat(userKeyword.getUser()).isEqualTo(user);
                    assertThat(userKeyword.getKeyword()).isEqualTo(keyword);
                });
    }

    @DisplayName("User 객체를 통해 keyword를 중복 추가할 경우 개수는 변하지 않는다")
    @Test
    void add_duplicate_keyword() {
        // given
        User user = User.create("testuser", "test@example.com", "password123", passwordEncoder);
        Keyword keyword = Keyword.create("테스트키워드");

        //when
        user.addUserKeyword(keyword);
        user.addUserKeyword(keyword);

        // then
        assertThat(user.getUserKeywords()).hasSize(1)
                .first()
                .satisfies(userKeyword -> {
                    assertThat(userKeyword.getUser()).isEqualTo(user);
                    assertThat(userKeyword.getKeyword()).isEqualTo(keyword);
                });
    }


    @DisplayName("User 객체를 통해 Keyword를 삭제할 수 있다")
    @Test
    void remove_keyword_from_user() {
        // given
        User user = User.create("testuser", "test@example.com", "password123", passwordEncoder);
        Keyword keyword = Keyword.create("테스트키워드");
        Keyword keyword2 = Keyword.create("테스트키워드2");

        user.addUserKeyword(keyword);
        user.addUserKeyword(keyword2);

        assertThat(user.getUserKeywords()).hasSize(2)
                .satisfiesExactly(
                        userKeyword -> {
                            assertThat(userKeyword.getUser()).isEqualTo(user);
                            assertThat(userKeyword.getKeyword()).isEqualTo(keyword);
                        },
                        userSite -> {
                            assertThat(userSite.getUser()).isEqualTo(user);
                            assertThat(userSite.getKeyword()).isEqualTo(keyword2);
                        }
                );

        // when
        user.removeUserKeyword(keyword);

        // then
        assertThat(user.getUserKeywords()).hasSize(1)
                .first()
                .satisfies(userKeyword -> {
                    assertThat(userKeyword.getUser()).isEqualTo(user);
                    assertThat(userKeyword.getKeyword()).isEqualTo(keyword2);
                });
    }
}