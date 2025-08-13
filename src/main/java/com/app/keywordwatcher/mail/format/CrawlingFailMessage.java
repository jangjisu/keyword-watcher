package com.app.keywordwatcher.mail.format;

import org.springframework.mail.SimpleMailMessage;

public class CrawlingFailMessage extends SimpleMailMessage {
    public static final String ADMIN_MAIL_ADDRESS = "jsjangdv@gmail.com";

    private CrawlingFailMessage(String userId, String url) {
        this.setTo(ADMIN_MAIL_ADDRESS);
        this.setSubject("userId: " + userId + "의 URL : " + url + " 크롤링 테스트가 실패했습니다.");
        this.setText("userId: " + userId + "의 크롤링 테스트가 실패했습니다.\n요청 URL: " + url);
    }

    public static CrawlingFailMessage create(String userId, String url) {
        return new CrawlingFailMessage(userId, url);
    }
}
