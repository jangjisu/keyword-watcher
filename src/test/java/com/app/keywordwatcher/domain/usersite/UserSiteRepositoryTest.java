package com.app.keywordwatcher.domain.usersite;

import com.app.keywordwatcher.domain.RepositoryTestSupport;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.site.SiteRepository;
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
class UserSiteRepositoryTest extends RepositoryTestSupport {
    @Autowired
    private UserSiteRepository userSiteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SiteRepository siteRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @DisplayName("findByUserId로 특정 유저가 사용하는 사이트들을 조회할 수 있다")
    @Test
    void findByUserId() {
        // given
        User user1 = userRepository.save(User.create("testuser", "test@example.com", "password123", passwordEncoder));
        User user2 = userRepository.save(User.create("testuser2", "test2@example.com", "password123", passwordEncoder));

        Site site1 = siteRepository.save(Site.create("https://site1.com", 1, 2));
        Site site2 = siteRepository.save(Site.create("https://site2.com", 1, 2));
        Site site3 = siteRepository.save(Site.create("https://site3.com", 1, 2));

        userSiteRepository.save(UserSite.create(user1, site1));
        userSiteRepository.save(UserSite.create(user1, site2));
        userSiteRepository.save(UserSite.create(user2, site3));

        // when
        List<UserSite> user1Sites = userSiteRepository.findByUserId(user1.getId());
        List<UserSite> user2Sites = userSiteRepository.findByUserId(user2.getId());

        // then
        assertThat(user1Sites).hasSize(2)
                .extracting(sk -> sk.getSite().getUrl())
                .containsExactlyInAnyOrder("https://site1.com", "https://site2.com");

        assertThat(user2Sites).hasSize(1)
                .extracting(sk -> sk.getSite().getUrl())
                .containsExactly("https://site3.com");
    }
}