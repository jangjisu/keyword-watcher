package com.app.keywordwatcher.crawler;

import com.app.keywordwatcher.domain.post.Post;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.exception.CrawlingParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultCrawlerTest {

    public static final String URL = "https://culture.seoul.go.kr/culture/bbs/B0000002/list.do?menuNo=200052";
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

    @DisplayName("크롤링 된 게시물 중 특정 날짜의 게시물을 가져올 수 있다.")
    @Test
    void get_date_post() {
        // given
        Site siteInfo = Site.create(URL, 1, 3);
        LocalDate createdAt = LocalDate.of(2025, 7, 16);

        // when
        List<Post> datePost = defaultCrawler.getDatePost(doc, siteInfo, createdAt);

        // then
        assertThat(datePost).hasSize(1)
                .containsExactlyInAnyOrder(
                        Post.createPost("[서울식물원] 여름행사 [식물원은 미술관] 자원봉사자 모집", createdAt)
                );
    }

    @DisplayName("크롤링 된 게시물 중 특정 날짜의 게시물 이 없는 경우 빈 리스트를 반환한다.")
    @Test
    void get_date_post_empty() {
        // given
        Site siteInfo = Site.create(URL, 1, 3);
        LocalDate createdAt = LocalDate.of(2025, 7, 17);

        // when
        List<Post> datePost = defaultCrawler.getDatePost(doc, siteInfo, createdAt);

        // then
        assertThat(datePost).isEmpty();
    }

    @DisplayName("크롤링 된 게시물 중 특정 날짜의 게시물을 가져올 수 있다.")
    @Test
    void get_date_post_invalid_position() {
        // given
        Site siteInfo = Site.create(URL, 1, 7);
        LocalDate createdAt = LocalDate.of(2025, 7, 16);

        // when // then
        assertThatThrownBy(() -> defaultCrawler.getDatePost(doc, siteInfo, createdAt))
                .isInstanceOf(CrawlingParseException.class)
                .hasMessage("Crawling parse error on " + URL + ". Expected at least 8 columns, but found 4.");
    }
}