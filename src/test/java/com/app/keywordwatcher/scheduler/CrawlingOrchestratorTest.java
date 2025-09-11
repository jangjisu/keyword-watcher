package com.app.keywordwatcher.scheduler;

import com.app.keywordwatcher.crawler.CrawlingHandler;
import com.app.keywordwatcher.domain.crawlinglog.CrawlingLogRepository;
import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.post.Post;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.site.SiteRepository;
import com.app.keywordwatcher.domain.user.User;
import com.app.keywordwatcher.domain.user.UserRepository;
import com.app.keywordwatcher.exception.CrawlingParseException;
import com.app.keywordwatcher.mail.CustomMailSender;
import com.app.keywordwatcher.mail.MailMessageMaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrawlingOrchestratorTest {

    @Mock
    private SiteRepository siteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CrawlingLogRepository crawlingLogRepository;

    @Mock
    private CrawlingHandler crawlingHandlerChain;

    @Mock
    private CustomMailSender customMailSender;

    @Mock
    private MailMessageMaker mailMessageMaker;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CrawlingOrchestrator crawlingOrchestrator;

    private Site testSite;
    private User testUser;
    private Post matchingPost;
    private Post matchingPostNotToday;
    private Post nonMatchingPost;

    @BeforeEach
    void setUp() {
        // Mock 설정
        String encoded = passwordEncoder.encode("password123");
        when(encoded).thenReturn("encodedPassword");

        // 테스트 데이터 설정
        testSite = Site.create("https://test.com");
        testUser = User.create("testUser", "test@example.com", "password123", passwordEncoder);
        testUser.addUserKeyword(Keyword.create("자바"));
        matchingPost = Post.createPost("자바 스프링 강의", LocalDate.now());
        matchingPostNotToday = Post.createPost("자바 스프링 강의", LocalDate.MIN);
        nonMatchingPost = Post.createPost("파이썬 강의", LocalDate.now());
    }

    @Test
    @DisplayName("사이트 목록이 비어있으면 크롤링을 수행하지 않는다")
    void notPerformCrawlingWhenNoSitesExist() {
        // Given
        when(siteRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        crawlingOrchestrator.performDailyCrawling();

        // Then
        verify(crawlingHandlerChain, never()).handle(any());
        verify(customMailSender, never()).sendMail(any());
    }

    @Test
    @DisplayName("크롤링 핸들러가 빈 포스트 목록을 반환하면 메일을 발송하지 않는다")
    void notSendMailWhenCrawlingReturnsEmptyPosts() {
        // Given
        when(siteRepository.findAll()).thenReturn(List.of(testSite));
        when(crawlingHandlerChain.handle(testSite))
                .thenReturn(Collections.emptyList());

        // When
        crawlingOrchestrator.performDailyCrawling();

        // Then
        verify(userRepository, never()).findUsersBySiteWithKeywords(any());
        verify(customMailSender, never()).sendMail(any());
    }

    @Test
    @DisplayName("사이트를 등록한 사용자가 없으면 메일을 발송하지 않는다")
    void notSendMailWhenNoUsersRegisteredForSite() {
        // Given
        when(siteRepository.findAll()).thenReturn(List.of(testSite));
        when(crawlingHandlerChain.handle(testSite))
                .thenReturn(List.of(matchingPost));
        when(userRepository.findUsersBySiteWithKeywords(testSite))
                .thenReturn(Collections.emptyList());

        // When
        crawlingOrchestrator.performDailyCrawling();

        // Then
        verify(customMailSender, never()).sendMail(any());
    }

    @Test
    @DisplayName("키워드가 매칭되는 포스트가 있고, 등록된 사용자가 있으면서, 오늘 날짜의 게시물이 아닌 경우 메일을 발송하지 않는다")
    void notSendMailWhenKeywordMatchesAndUserExistsButNotMatchingDate() {
        // Given
        when(siteRepository.findAll()).thenReturn(List.of(testSite));
        when(crawlingHandlerChain.handle(testSite))
                .thenReturn(List.of(matchingPostNotToday));

        // When
        crawlingOrchestrator.performDailyCrawling();

        // Then
        verify(customMailSender, never()).sendMail(any());
    }

    @Test
    @DisplayName("키워드가 매칭되는 포스트가 있고, 등록된 사용자가 있으면서, 오늘 날짜의 게시물을 찾은 경우 메일을 발송한다")
    void sendMailWhenKeywordMatchesAndUserExistsAndDayMatch() {
        // Given
        when(siteRepository.findAll()).thenReturn(List.of(testSite));
        when(crawlingHandlerChain.handle(testSite))
                .thenReturn(List.of(matchingPost));
        when(userRepository.findUsersBySiteWithKeywords(testSite))
                .thenReturn(List.of(testUser));
        when(mailMessageMaker.makeNewPostAlertMessage(anyString(), anyString(), anyString()))
                .thenReturn(new SimpleMailMessage());

        // When
        crawlingOrchestrator.performDailyCrawling();

        // Then
        verify(customMailSender, times(1)).sendMail(any(SimpleMailMessage.class));
        verify(crawlingLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("키워드가 매칭되지 않는 포스트의 경우 메일을 발송하지 않는다")
    void notSendMailWhenKeywordDoesNotMatch() {
        // Given
        when(siteRepository.findAll()).thenReturn(List.of(testSite));
        when(crawlingHandlerChain.handle(testSite))
                .thenReturn(List.of(nonMatchingPost));
        when(userRepository.findUsersBySiteWithKeywords(testSite))
                .thenReturn(List.of(testUser));

        // When
        crawlingOrchestrator.performDailyCrawling();

        // Then
        verify(customMailSender, never()).sendMail(any());
    }

    @Test
    @DisplayName("크롤링 중 예외가 발생하면 실패 알림 메일을 발송한다")
    void sendFailureNotificationWhenCrawlingThrowsException() {
        // Given
        CrawlingParseException expectedException = new CrawlingParseException("크롤링 실패");
        when(siteRepository.findAll()).thenReturn(List.of(testSite));
        when(crawlingHandlerChain.handle(testSite))
                .thenThrow(expectedException);
        when(mailMessageMaker.makeCrawlingTestFailMessage(anyString(), anyString()))
                .thenReturn(new SimpleMailMessage());

        // When
        crawlingOrchestrator.performDailyCrawling();

        // Then
        verify(mailMessageMaker).makeCrawlingTestFailMessage(
                "SYSTEM",
                testSite.getUrl()
        );
    }

    @Test
    @DisplayName("여러 사이트가 있을 때 각각 독립적으로 처리한다")
    void processMultipleSitesIndependently() {
        // Given
        Site site1 = Site.create("https://site1.com");
        Site site2 = Site.create("https://site2.com");

        when(siteRepository.findAll()).thenReturn(List.of(site1, site2));
        when(crawlingHandlerChain.handle(site1))
                .thenReturn(List.of(matchingPost));
        when(crawlingHandlerChain.handle(site2))
                .thenThrow(new RuntimeException("site2 크롤링 실패"));
        when(userRepository.findUsersBySiteWithKeywords(site1))
                .thenReturn(List.of(testUser));
        when(mailMessageMaker.makeNewPostAlertMessage(anyString(), anyString(), anyString()))
                .thenReturn(new SimpleMailMessage());
        when(mailMessageMaker.makeCrawlingTestFailMessage(anyString(), anyString()))
                .thenReturn(new SimpleMailMessage());

        // When
        crawlingOrchestrator.performDailyCrawling();

        // Then
        verify(customMailSender, times(2)).sendMail(any(SimpleMailMessage.class)); // 성공 메일 1개 + 실패 메일 1개
        verify(crawlingLogRepository, times(2)).save(any()); // 성공 로그 1개 + 실패 로그 1개
    }
}
