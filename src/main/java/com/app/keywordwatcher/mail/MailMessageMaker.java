package com.app.keywordwatcher.mail;

import org.springframework.mail.SimpleMailMessage;

public class MailMessageMaker {
    public static final String ADMIN_MAIL_ADDRESS = "jsjangdv@gmail.com";

    public SimpleMailMessage makeCrawlingTestFailMessage(String userId, String url) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(ADMIN_MAIL_ADDRESS);
        message.setSubject("크롤링 테스트 실패 알림");
        message.setText("userId: " + userId + "의 크롤링 테스트가 실패했습니다.\n요청 URL: " + url);
        return message;
    }

    // 새 글 알림 메일 생성
    public SimpleMailMessage makeNewPostAlertMessage(String userEmail, String siteUrl, String postTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("[" + siteUrl + "] 에 새 글 " + postTitle + "이 등록되었습니다");
        message.setText("[" + siteUrl + "] 에 새 글 " + postTitle + "이 등록되었습니다");
        return message;
    }
}
