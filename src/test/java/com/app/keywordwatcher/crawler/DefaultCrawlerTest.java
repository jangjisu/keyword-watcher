package com.app.keywordwatcher.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultCrawlerTest {

    static Document doc;

    Document emptyDoc;

    Document emptyTableDoc = Jsoup.parse("<html><body><table><tbody></tbody></table></body></html>");

    private DefaultCrawler defaultCrawler = new DefaultCrawler();

    @BeforeAll
    static void setUp() throws IOException {
        try (var is = DefaultCrawlerTest.class.getClassLoader().getResourceAsStream("defaultCrawlPage.html")) {
            assertNotNull(is, "테스트 리소스 파일을 찾을 수 없습니다.");
            String html = new String(is.readAllBytes());
            doc = Jsoup.parse(html);
        }
    }


    @DisplayName("크롤링 된 페이지의 글이 있으면 정보를 가져올 수 있다.")
    @Test
    void can_handle() {
        // given // when
        boolean cannotHandle = defaultCrawler.cannotHandle(doc);

        // then
        assertThat(cannotHandle).isFalse();
    }

    @DisplayName("크롤링 된 페이지가 Null인 경우 정보를 가져올 수 없다.")
    @Test
    void cannot_handle_null() {
        // given // when
        boolean cannotHandle = defaultCrawler.cannotHandle(emptyDoc);

        // then
        assertThat(cannotHandle).isTrue();
    }

    @DisplayName("크롤링 된 페이지에 Table이 없을 경우 정보를 가져올 수 없다.")
    @Test
    void cannot_handle_table_null() {
        // given // when
        boolean cannotHandle = defaultCrawler.cannotHandle(emptyTableDoc);

        // then
        assertThat(cannotHandle).isTrue();
    }

//    TODO 삭제
//    @DisplayName("크롤링 테스트")
//    @Test
//    void crawlingTest() throws IOException {
//        //given
//        String url = "https://culture.seoul.go.kr/culture/bbs/B0000002/list.do?menuNo=200052";
//
//        Document doc = Jsoup.connect(url)
//                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
//                .timeout(10000)
//                .get();
//
//        System.out.println("크롤링 데이터: " + doc);
//
//        // 1. 페이지 제목 확인
//        System.out.println("페이지 제목: " + doc.title());
//
//        // 2. 게시글 목록 구조 파악
//        Elements posts = doc.select("table tbody tr"); // 일반적인 게시판 구조
//
//        System.out.println("찾은 게시글 수: " + posts.size());
//
//        // 3. 각 게시글 정보 추출
//        for (int i = 0; i < Math.min(posts.size(), 5); i++) { // 상위 5개만
//            Element post = posts.get(i);
//
//            System.out.println("=== 게시글 " + (i + 1) + " ===");
//            System.out.println("전체 HTML: " + post.html());
//
//            // 제목 추출 시도 (여러 선택자로)
//            Element titleElement = post.select("a").first();
//            if (titleElement != null) {
//                System.out.println("제목: " + titleElement.text());
//                System.out.println("링크: " + titleElement.attr("href"));
//            }
//
//            // 날짜 정보 추출 시도
//            Elements dateElements = post.select("td");
//            System.out.println("컬럼 개수: " + dateElements.size());
//            for (int j = 0; j < dateElements.size(); j++) {
//                System.out.println("컬럼 " + j + ": " + dateElements.get(j).text());
//            }
//        }
//
//        // when
//
//        // then
//    }

}