package com.app.keywordwatcher.domain.post;

import lombok.Getter;
import org.jsoup.select.Elements;

@Getter
public class PostIdx {
    private int titleIdx;
    private int dateIdx;

    private static boolean isTitleHeader(String headerText) {
        return headerText.contains("제목");
    }

    private static boolean isDateHeader(String headerText) {
        return headerText.contains("작성일") || headerText.contains("등록일");
    }

    private PostIdx(int titleIdx, int dateIdx) {
        this.titleIdx = titleIdx;
        this.dateIdx = dateIdx;
    }

    public static PostIdx getPostIdxFromHeader(Elements headers) {
        int titleIdx = -1;
        int dateIdx = -1;

        for (int i = 0; i < headers.size(); i++) {
            if (isTitleHeader(headers.get(i).text())) {
                titleIdx = i;
            }

            if (isDateHeader(headers.get(i).text())) {
                dateIdx = i;
            }
        }

        if (titleIdx == -1 || dateIdx == -1) {
            return null;
        }

        return new PostIdx(titleIdx, dateIdx);
    }

    public boolean isInValid() {
        return titleIdx == -1 || dateIdx == -1;
    }

    public boolean isValid() {
        return !isInValid();
    }
}
