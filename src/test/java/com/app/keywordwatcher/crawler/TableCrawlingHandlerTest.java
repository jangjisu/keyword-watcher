package com.app.keywordwatcher.crawler;

import com.app.keywordwatcher.domain.post.Post;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.exception.CrawlingParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class TableCrawlingHandlerTest {

    static Document doc;
    static Site siteInfo;
    static LocalDate testDate = LocalDate.of(2025, 7, 16);

    Document emptyDoc = Jsoup.parse("<html><body></body></html>");

    @BeforeAll
    static void setUp() throws IOException {
        try (var is = TableCrawlingHandlerTest.class.getClassLoader().getResourceAsStream("TableCrawlPage.html")) {
            assertNotNull(is, "테스트 리소스 파일을 찾을 수 없습니다.");
            String html = new String(is.readAllBytes());
            doc = Jsoup.parse(html);
        }

        String url = "https://culture.seoul.go.kr/culture/bbs/B0000002/list.do?menuNo=200052";
        siteInfo = Site.create(url);
    }

    @DisplayName("키워드가 포함된 게시글이 있을 때 정상적으로 Post 목록을 반환한다")
    @Test
    void matching_posts_date() {
        // given
        TableCrawlingHandler handler = new TableCrawlingHandler(null);

        // when
        List<Post> result = handler.handle(doc, siteInfo, testDate);

        // then
        assertThat(result).hasSize(1)
                .extracting(Post::getTitle)
                .contains("[서울식물원] 여름행사 [식물원은 미술관] 자원봉사자 모집");

    }

    @DisplayName("날짜가 일치하는 게시물이 없으면 빈 목록을 반환한다")
    @Test
    void no_matching_date() {
        // given
        TableCrawlingHandler handler = new TableCrawlingHandler(null);
        LocalDate noMatchDate = LocalDate.of(2025, 1, 1);

        // when
        List<Post> result = handler.handle(doc, siteInfo, noMatchDate);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("doc 이 null인 경우 예외를 던진다")
    @Test
    void doc_null() {
        // given
        TableCrawlingHandler handler = new TableCrawlingHandler(null);

        // when // then
        assertThatThrownBy(() -> handler.handle(null, siteInfo, testDate))
                .isInstanceOf(CrawlingParseException.class)
                .hasMessageContaining("Document cannot be null");
    }

    @DisplayName("지원하지 않는 형식의 문서가 들어오면 다음 핸들러로 위임한다")
    @Test
    void handle_delegates_to_next_handler() {
        // given
        CrawlingHandler mockNextHandler = Mockito.mock(CrawlingHandler.class);
        List<Post> expectedResult = List.of(Post.createPost("Test Post", testDate));
        when(mockNextHandler.handle(emptyDoc, siteInfo, testDate)).thenReturn(expectedResult);

        TableCrawlingHandler handler = new TableCrawlingHandler(mockNextHandler);

        // when
        List<Post> result = handler.handle(emptyDoc, siteInfo, testDate);

        // then
        assertThat(result).isEqualTo(expectedResult);
        Mockito.verify(mockNextHandler).handle(emptyDoc, siteInfo, testDate);
    }

    @DisplayName("다음 핸들러가 존재하지 않는다면 지원하지 않는 문서형식 예외를 던진다")
    @Test
    void handle_throws_exception_when_no_next_handler() {
        // given
        TableCrawlingHandler handler = new TableCrawlingHandler(null);

        // when & then
        assertThatThrownBy(() -> handler.handle(emptyDoc, siteInfo, testDate))
                .isInstanceOf(CrawlingParseException.class)
                .hasMessageContaining("Unsupported document format for " + siteInfo.getUrl());
    }

    @DisplayName("테이블이 있는 문서를 처리할 수 있으면 true를 반환한다")
    @Test
    void canHandle_returns_true_when_can_process() {
        // given
        TableCrawlingHandler handler = new TableCrawlingHandler(null);

        // when
        boolean result = handler.canHandle(doc);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("테이블이 없고 다음 핸들러가 없으면 false를 반환한다")
    @Test
    void canHandle_returns_false_when_cannot_process_and_no_next_handler() {
        // given
        TableCrawlingHandler handler = new TableCrawlingHandler(null);

        // when
        boolean result = handler.canHandle(emptyDoc);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("테이블이 없지만 다음 핸들러가 있으면 다음 핸들러로 위임한다")
    @Test
    void canHandle_delegates_to_next_handler_when_cannot_process() {
        // given
        CrawlingHandler mockNextHandler = Mockito.mock(CrawlingHandler.class);
        when(mockNextHandler.canHandle(emptyDoc)).thenReturn(true);

        TableCrawlingHandler handler = new TableCrawlingHandler(mockNextHandler);

        // when
        boolean result = handler.canHandle(emptyDoc);

        // then
        assertThat(result).isTrue();
        Mockito.verify(mockNextHandler).canHandle(emptyDoc);
    }
}