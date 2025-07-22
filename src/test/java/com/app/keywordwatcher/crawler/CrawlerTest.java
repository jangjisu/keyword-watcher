package com.app.keywordwatcher.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CrawlerTest {

    @DisplayName("크롤링 테스트")
    @Test
    void crawlingTest() {
        //given
        try {
            String url = "https://culture.seoul.go.kr/culture/bbs/B0000002/list.do?menuNo=200052";

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            // 1. 페이지 제목 확인
            System.out.println("페이지 제목: " + doc.title());

            // 2. 게시글 목록 구조 파악
            Elements posts = doc.select("table tbody tr"); // 일반적인 게시판 구조

            System.out.println("찾은 게시글 수: " + posts.size());

            // 3. 각 게시글 정보 추출 시도
            for (int i = 0; i < Math.min(posts.size(), 5); i++) { // 상위 5개만
                Element post = posts.get(i);

                System.out.println("=== 게시글 " + (i + 1) + " ===");
                System.out.println("전체 HTML: " + post.html());

                // 제목 추출 시도 (여러 선택자로)
                Element titleElement = post.select("a").first();
                if (titleElement != null) {
                    System.out.println("제목: " + titleElement.text());
                    System.out.println("링크: " + titleElement.attr("href"));
                }

                // 날짜 정보 추출 시도
                Elements dateElements = post.select("td");
                System.out.println("컬럼 개수: " + dateElements.size());
                for (int j = 0; j < dateElements.size(); j++) {
                    System.out.println("컬럼 " + j + ": " + dateElements.get(j).text());
                }
            }

        } catch (Exception e) {
            System.out.println("크롤링 실패: " + e);
        }

        // when

        // then
    }

}