package com.app.keywordwatcher.domain.crawlinglog;

import com.app.keywordwatcher.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CrawlingLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String siteUrl;
    private String postTitle;
    private String userEmail;
    private String keyword;
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    private LogType logType;

    private LocalDateTime loggedAt;

    public enum LogType {
        MAIL_SENT_SUCCESS,
        MAIL_SENT_FAIL,
        CRAWLING_FAIL
    }

    private CrawlingLog(String siteUrl, String postTitle, String userEmail, String keyword, LogType logType) {
        this.siteUrl = siteUrl;
        this.postTitle = postTitle;
        this.userEmail = userEmail;
        this.keyword = keyword;
        this.logType = logType;
        this.loggedAt = LocalDateTime.now();
    }

    private CrawlingLog(String siteUrl, String errorMessage, LogType logType) {
        this.siteUrl = siteUrl;
        this.errorMessage = errorMessage;
        this.logType = logType;
        this.loggedAt = LocalDateTime.now();
    }

    private CrawlingLog(String siteUrl, String postTitle, String userEmail, String errorMessage) {
        this.siteUrl = siteUrl;
        this.postTitle = postTitle;
        this.userEmail = userEmail;
        this.errorMessage = errorMessage;
        this.logType = LogType.MAIL_SENT_FAIL;
        this.loggedAt = LocalDateTime.now();
    }

    public static CrawlingLog createMailSentLog(String siteUrl, String postTitle, String userEmail, String keyword) {
        return new CrawlingLog(siteUrl, postTitle, userEmail, keyword, LogType.MAIL_SENT_SUCCESS);
    }

    public static CrawlingLog createMailFailLog(String siteUrl, String postTitle, String userEmail, String errorMessage) {
        return new CrawlingLog(siteUrl, postTitle, userEmail, errorMessage);
    }

    public static CrawlingLog createCrawlingFailLog(String siteUrl, String errorMessage) {
        return new CrawlingLog(siteUrl, errorMessage, LogType.CRAWLING_FAIL);
    }
}
