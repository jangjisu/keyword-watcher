package com.app.keywordwatcher.scheduler;

import com.app.keywordwatcher.crawler.CrawlingHandler;
import com.app.keywordwatcher.domain.crawlinglog.CrawlingLog;
import com.app.keywordwatcher.domain.crawlinglog.CrawlingLogRepository;
import com.app.keywordwatcher.domain.post.Post;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.site.SiteRepository;
import com.app.keywordwatcher.domain.user.User;
import com.app.keywordwatcher.domain.user.UserRepository;
import com.app.keywordwatcher.mail.CustomMailSender;
import com.app.keywordwatcher.mail.MailMessageMaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrawlingOrchestrator {

    private final UserRepository userRepository;
    private final SiteRepository siteRepository;
    private final CrawlingLogRepository crawlingLogRepository;
    private final CrawlingHandler crawlingHandlerChain;
    private final CustomMailSender customMailSender;
    private final MailMessageMaker mailMessageMaker;

    /**
     * 일일 크롤링 작업 수행
     * 1. 전체 사이트 조회
     * 2. 각 사이트에 대해 크롤링 수행
     * 3. 해당 사이트를 등록한 사용자들의 키워드와 매칭하여 메일 발송
     */
    @Transactional
    public void performDailyCrawling() {
        log.info("=== 일일 크롤링 작업 시작 ===");

        try {
            // 1. 전체 사이트 조회
            List<Site> allSites = siteRepository.findAll();
            log.info("총 {} 개의 사이트에 대해 크롤링 수행", allSites.size());

            // 2. 각 사이트에 대해 크롤링 수행
            for (Site site : allSites) {
                processCrawlingForSite(site);
            }

            log.info("=== 일일 크롤링 작업 완료 ===");
        } catch (Exception e) {
            log.error("크롤링 작업 실행 중 오류 발생", e);
        }
    }

    /**
     * 특정 사이트에 대한 크롤링 처리
     */
    private void processCrawlingForSite(Site site) {
        try {
            log.info("사이트 크롤링 시작: {}", site.getUrl());

            List<Post> posts = crawlingHandlerChain.handle(site);

            if (posts.isEmpty()) {
                log.info("사이트 {}에서 크롤링된 글이 없습니다.", site.getUrl());
                return;
            }

            List<Post> newPosts = posts.stream()
                    .filter(post -> post.getCreateAt().isEqual(LocalDate.now()))
                    .toList();

            if (newPosts.isEmpty()) {
                log.info("사이트 {}에서 오늘 날짜의 글이 없습니다.", site.getUrl());
                return;
            }

            log.info("사이트 {} 크롤링 완료 - {} 개의 새 글 발견", site.getUrl(), newPosts.size());

            List<User> siteUsers = getUsersBySite(site);
            log.info("사이트 {}를 등록한 사용자 {} 명", site.getUrl(), siteUsers.size());

            for (Post post : posts) {
                processPostForUsers(site, post, siteUsers);
            }

        } catch (Exception e) {
            log.error("사이트 {} 크롤링 중 오류 발생", site.getUrl(), e);

            // 크롤링 실패 로그 저장
            saveCrawlingFailureLog(site, e);

            // 관리자에게 실패 알림 메일 발송
            sendCrawlingFailureNotification(site);
        }
    }

    /**
     * 특정 사이트를 등록한 사용자들 조회
     */
    private List<User> getUsersBySite(Site site) {
        return userRepository.findUsersBySiteWithKeywords(site);
    }

    /**
     * 특정 글에 대해 사용자들의 키워드 매칭 확인 및 메일 발송
     */
    private void processPostForUsers(Site site, Post post, List<User> users) {
        for (User user : users) {
            // 사용자의 키워드와 글 제목 매칭 확인
            String matchedKeyword = user.getUserKeywords().stream()
                    .filter(userKeyword ->
                            post.getTitle().toLowerCase().contains(
                                    userKeyword.getKeyword().getKeyText().toLowerCase()))
                    .map(userKeyword -> userKeyword.getKeyword().getKeyText())
                    .findFirst()
                    .orElse(null);

            if (matchedKeyword != null) {
                sendNewPostNotification(user, site, post, matchedKeyword);
                log.info("키워드 매칭 - 사용자: {}, 글: {}, 키워드: {}",
                        user.getUserId(), post.getTitle(), matchedKeyword);
            }
        }
    }

    /**
     * 새 글 알림 메일 발송
     */
    private void sendNewPostNotification(User user, Site site, Post post, String matchedKeyword) {
        try {
            SimpleMailMessage message = mailMessageMaker.makeNewPostAlertMessage(
                    user.getEmail(),
                    site.getUrl(),
                    post.getTitle()
            );

            customMailSender.sendMail(message);
            log.info("새 글 알림 메일 발송 완료: {} -> {}", post.getTitle(), user.getEmail());

            // 메일 발송 성공 로그 저장
            CrawlingLog successLog = CrawlingLog.createMailSentLog(
                    site.getUrl(),
                    post.getTitle(),
                    user.getEmail(),
                    matchedKeyword
            );
            crawlingLogRepository.save(successLog);

        } catch (Exception e) {
            log.error("새 글 알림 메일 발송 실패: {} (사용자: {})", post.getTitle(), user.getEmail(), e);

            // 메일 발송 실패 로그 저장
            CrawlingLog failLog = CrawlingLog.createMailFailLog(
                    site.getUrl(),
                    post.getTitle(),
                    user.getEmail(),
                    e.getMessage()
            );
            crawlingLogRepository.save(failLog);
        }
    }

    /**
     * 크롤링 실패 알림 메일 발송
     */
    private void sendCrawlingFailureNotification(Site site) {
        try {
            SimpleMailMessage message = mailMessageMaker.makeCrawlingTestFailMessage(
                    "SYSTEM",
                    site.getUrl()
            );

            customMailSender.sendMail(message);
            log.info("크롤링 실패 알림 메일 발송 완료: {}", site.getUrl());

        } catch (Exception e) {
            log.error("크롤링 실패 알림 메일 발송 실패: {}", site.getUrl(), e);
        }
    }

    /**
     * 크롤링 실패 로그 저장
     */
    private void saveCrawlingFailureLog(Site site, Exception exception) {
        CrawlingLog failLog = CrawlingLog.createCrawlingFailLog(
                site.getUrl(),
                exception.getMessage()
        );
        crawlingLogRepository.save(failLog);
    }
}