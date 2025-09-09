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
        List<Post> result = handler.handle(doc);

        // then
        assertThat(result).hasSize(10);
    }

    @DisplayName("doc 이 null인 경우 예외를 던진다")
    @Test
    void doc_null() {
        // given
        IframeCrawlingHandler handler = new IframeCrawlingHandler(null);

        Document nullDoc = null;

        // when // then
        assertThatThrownBy(() -> handler.handle(nullDoc))
                .isInstanceOf(CrawlingParseException.class)
                .hasMessageContaining("Document cannot be null");
    }

    @DisplayName("iframe이 없는 문서가 들어오면 다음 핸들러로 위임한다")
    @Test
    void handle_delegates_to_next_handler() {
        // given
        CrawlingHandler mockNextHandler = Mockito.mock(CrawlingHandler.class);
        List<Post> expectedResult = List.of(Post.createPost("Test Post", testDate));
        when(mockNextHandler.handle(emptyDoc)).thenReturn(expectedResult);

        IframeCrawlingHandler handler = new IframeCrawlingHandler(mockNextHandler);

        // when
        List<Post> result = handler.handle(emptyDoc);

        // then
        assertThat(result).isEqualTo(expectedResult);
        Mockito.verify(mockNextHandler).handle(emptyDoc);
    }

    @DisplayName("다음 핸들러가 존재하지 않는다면 핸들러가 존재하지 않는 예외를 던진다")
    @Test
    void handle_throws_exception_when_no_next_handler() {
        // given
        IframeCrawlingHandler handler = new IframeCrawlingHandler(null);

        // when & then
        assertThatThrownBy(() -> handler.handle(emptyDoc))
                .isInstanceOf(CrawlingParseException.class)
                .hasMessageContaining("No More Handler");
    }

    @DisplayName("iframe이 있는 문서를 처리할 수 있으면 true를 반환한다")
    @Test
    void canHandle_returns_true_when_can_process() {
        // given
        crawlingUtilMock.when(() -> CrawlingUtil.fetchDocument(anyString())).thenReturn(iframeDoc);
        IframeCrawlingHandler handler = new IframeCrawlingHandler(null);

        // when
        boolean result = handler.canHandle(doc);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("iframe이 없고 다음 핸들러가 없으면 false를 반환한다")
    @Test
    void canHandle_returns_false_when_cannot_process_and_no_next_handler() {
        // given
        IframeCrawlingHandler handler = new IframeCrawlingHandler(null);

        // when
        boolean result = handler.canHandle(emptyDoc);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("iframe이 없지만 다음 핸들러가 있으면 다음 핸들러로 위임한다")
    @Test
    void canHandle_delegates_to_next_handler_when_cannot_process() {
        // given
        CrawlingHandler mockNextHandler = Mockito.mock(CrawlingHandler.class);
        when(mockNextHandler.canHandle(emptyDoc)).thenReturn(true);

        IframeCrawlingHandler handler = new IframeCrawlingHandler(mockNextHandler);

        // when
        boolean result = handler.canHandle(emptyDoc);

        // then
        assertThat(result).isTrue();
        Mockito.verify(mockNextHandler).canHandle(emptyDoc);
    }
}