package com.app.keywordwatcher.mail.format;

import org.springframework.mail.SimpleMailMessage;

public class NewPostAlertMessage extends SimpleMailMessage {
    private NewPostAlertMessage(String userEmail, String siteUrl, String postTitle) {
        this.setTo(userEmail);
        this.setSubject("[" + siteUrl + "] 에 새 글 " + postTitle + "이 등록되었습니다");
        this.setText("[" + siteUrl + "] 에 새 글 " + postTitle + "이 등록되었습니다");
    }

    public static NewPostAlertMessage create(String userEmail, String siteUrl, String postTitle) {
        return new NewPostAlertMessage(userEmail, siteUrl, postTitle);
    }
}
