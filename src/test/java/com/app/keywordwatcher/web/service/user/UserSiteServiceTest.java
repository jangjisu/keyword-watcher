package com.app.keywordwatcher.web.service.user;

import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.site.SiteRepository;
import com.app.keywordwatcher.domain.user.User;
import com.app.keywordwatcher.domain.user.UserRepository;
import com.app.keywordwatcher.web.controller.user.site.request.SiteRequest;
import com.app.keywordwatcher.web.service.ServiceTestSupport;
import com.app.keywordwatcher.web.service.user.response.SiteResponse;
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
class UserSiteServiceTest extends ServiceTestSupport {
    @Autowired
    private UserSiteService userSiteService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SiteRepository siteRepository;

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

        Site site = Site.create("https://example.com", 2, 3);
        siteRepository.save(site);

        Site site1 = Site.create("https://test.com", 3, 4);
        siteRepository.save(site1);

        entityManager.flush();

        user.addUserSite(site);
        user.addUserSite(site1);

        entityManager.flush();
    }

    @DisplayName("존재하는 유저의 사이트를 조회할 수 있다")
    @Test
    void get_user_sites() {
        // given
        List<SiteResponse> sites = userSiteService.getUserSites("existUser");

        // when // then
        assertThat(sites).hasSize(2)
                .extracting("url")
                .containsExactlyInAnyOrder("https://example.com", "https://test.com");
    }

    @DisplayName("존재하지 않는 유저의 사이트를 조회하면 에러가 발생한다")
    @Test
    void get_non_exist_user_sites() {
        // given // when // then
        assertThatThrownBy(() -> userSiteService.getUserSites("nonExistUser"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("이미 등록된 사이트를 추가할 경우, 유저 사이트가 등록된다.")
    @Test
    void add_exist_site() {
        // given
        SiteRequest existSite = SiteRequest.builder()
                .url("https://example.com")
                .build();

        // when
        userSiteService.addSite("existUser2", existSite);

        // then
        List<Site> sites = siteRepository.findAll();
        assertThat(sites).hasSize(2)
                .extracting("url")
                .containsExactlyInAnyOrder("https://example.com", "https://test.com");

        Optional<User> user = userRepository.findByUserId("existUser2");
        assertThat(user).isPresent();
        assertThat(user.get().getUserSites()).hasSize(1)
                .extracting(us -> us.getSite().getUrl())
                .containsExactly("https://example.com");
    }

    @DisplayName("새로운 사이트를 추가할 경우, 사이트와 유저 사이트가 모두 등록된다.")
    @Test
    void add_new_site() {
        // given
        SiteRequest newSite = SiteRequest.builder()
                .url("https://newssite.com")
                .titleIndex(2)
                .createAtIndex(3)
                .build();

        // when
        userSiteService.addSite("existUser2", newSite);

        // then
        List<Site> sites = siteRepository.findAll();
        assertThat(sites).hasSize(3)
                .extracting("url")
                .containsExactlyInAnyOrder("https://example.com", "https://test.com", "https://newssite.com");

        Optional<User> user = userRepository.findByUserId("existUser2");
        assertThat(user).isPresent();
        assertThat(user.get().getUserSites()).hasSize(1)
                .extracting(us -> us.getSite().getUrl())
                .containsExactly("https://newssite.com");
    }

    @DisplayName("존재하지 않는 유저에게 사이트 추가 요청 시 에러가 발생한다")
    @Test
    void add_site_to_non_exist_user() {
        // given
        SiteRequest siteRequest = SiteRequest.builder()
                .url("https://example.com")
                .build();

        // when // then
        assertThatThrownBy(() -> userSiteService.addSite("nonExistUser", siteRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("유저의 사이트를 삭제할 수 있다.")
    @Test
    void remove_user_site() {
        // given
        SiteRequest siteRequest = SiteRequest.builder()
                .url("https://example.com")
                .build();

        // when
        userSiteService.removeSite("existUser", siteRequest);

        // then
        Optional<User> user = userRepository.findByUserId("existUser");
        assertThat(user).isPresent();
        assertThat(user.get().getUserSites()).hasSize(1)
                .extracting(us -> us.getSite().getUrl())
                .containsExactly("https://test.com");
    }

    @DisplayName("존재하지 않는 사이트를 삭제하려고 하면 에러가 발생한다")
    @Test
    void remove_non_exist_site() {
        // given
        SiteRequest siteRequest = SiteRequest.builder()
                .url("https://nonexist.com")
                .build();

        // when // then
        assertThatThrownBy(() -> userSiteService.removeSite("existUser", siteRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사이트를 찾을 수 없습니다.");
    }

    @DisplayName("존재하지 않는 유저에게 사이트 삭제 요청 시 에러가 발생한다")
    @Test
    void remove_site_from_non_exist_user() {
        // given
        SiteRequest siteRequest = SiteRequest.builder()
                .url("https://example.com")
                .build();

        // when // then
        assertThatThrownBy(() -> userSiteService.removeSite("nonExistUser", siteRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }
}
