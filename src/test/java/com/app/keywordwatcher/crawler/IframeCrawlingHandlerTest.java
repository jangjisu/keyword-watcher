package com.app.keywordwatcher.crawler;

import com.app.keywordwatcher.domain.post.Post;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.exception.CrawlingParseException;
import com.app.keywordwatcher.util.CrawlingUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IframeCrawlingHandlerTest {

    static Document doc;
    static Document iframeDoc;
    static Site siteInfo;
    static LocalDate testDate = LocalDate.of(2025, 8, 1);

    Document emptyDoc = Jsoup.parse("<html><body></body></html>");
    MockedStatic<CrawlingUtil> crawlingUtilMock;

    @BeforeAll
    static void setUp() throws IOException {
        try (var is = IframeCrawlingHandlerTest.class.getClassLoader().getResourceAsStream("IframeCrawlPage.html")) {
            assertNotNull(is, "테스트 리소스 파일을 찾을 수 없습니다.");
            String html = new String(is.readAllBytes());
            doc = Jsoup.parse(html);
        }

        try (var is = IframeCrawlingHandlerTest.class.getClassLoader().getResourceAsStream("IframeInCrawlPage.html")) {
            assertNotNull(is, "테스트 리소스 파일을 찾을 수 없습니다.");
            String html = new String(is.readAllBytes());
            iframeDoc = Jsoup.parse(html);
        }

        String url = "https://www.gangnam.go.kr/contents/employment/1/view.do?mid=ID05_0418";
        siteInfo = Site.create(url);
    }

    @BeforeEach
    void setUpMocks() {
        crawlingUtilMock = Mockito.mockStatic(CrawlingUtil.class);
    }

    @AfterEach
    void closeMocks() {
        if (crawlingUtilMock != null) {
            crawlingUtilMock.close();
        }
    }

    @DisplayName("키워드가 포함된 게시글이 있을 때 정상적으로 Post 목록을 반환한다")
    @Test
    void matching_posts_date() {
        // given
        crawlingUtilMock.when(() -> CrawlingUtil.fetchDocument(anyString())).thenReturn(iframeDoc);
        IframeCrawlingHandler handler = new IframeCrawlingHandler(null);

        // when
        List<Post> result = handler.handle(doc, siteInfo, testDate);

        // then
        assertThat(result).hasSize(1)
                .extracting(Post::getTitle)
                .contains("서울특별시 강남구 농지이용관리지원 기간제근로자 채용 서류전형 합격자 공고");
    }

    @DisplayName("날짜가 일치하는 게시물이 없으면 빈 목록을 반환한다")
    @Test
    void no_matching_date() {
        // given
        crawlingUtilMock.when(() -> CrawlingUtil.fetchDocument(anyString())).thenReturn(iframeDoc);
        IframeCrawlingHandler handler = new IframeCrawlingHandler(null);
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
        IframeCrawlingHandler handler = new IframeCrawlingHandler(null);

        // when // then
        assertThatThrownBy(() -> handler.handle(null, siteInfo, testDate))
                .isInstanceOf(CrawlingParseException.class)
                .hasMessageContaining("Document cannot be null");
    }

    @DisplayName("iframe이 없는 문서가 들어오면 다음 핸들러로 위임한다")
    @Test
    void handle_delegates_to_next_handler() {
        // given
        CrawlingHandler mockNextHandler = Mockito.mock(CrawlingHandler.class);
        List<Post> expectedResult = List.of(Post.createPost("Test Post", testDate));
        when(mockNextHandler.handle(emptyDoc, siteInfo, testDate)).thenReturn(expectedResult);

        IframeCrawlingHandler handler = new IframeCrawlingHandler(mockNextHandler);

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
        IframeCrawlingHandler handler = new IframeCrawlingHandler(null);

        // when & then
        assertThatThrownBy(() -> handler.handle(emptyDoc, siteInfo, testDate))
                .isInstanceOf(CrawlingParseException.class)
                .hasMessageContaining("Unsupported document format for " + siteInfo.getUrl());
    }
}