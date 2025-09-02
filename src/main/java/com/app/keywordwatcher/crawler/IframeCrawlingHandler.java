package com.app.keywordwatcher.crawler;

import com.app.keywordwatcher.domain.post.Post;
import com.app.keywordwatcher.domain.post.PostIdx;
import com.app.keywordwatcher.util.CrawlingUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class IframeCrawlingHandler extends CrawlingHandler {

    public IframeCrawlingHandler(CrawlingHandler nextHandler) {
        super(nextHandler);
    }

    @Override
    protected List<Post> extractPosts(Document doc) {
        Elements iframes = doc.select("iframe");

        if (iframes.isEmpty()) {
            return List.of();
        }

        List<Post> allRows = new ArrayList<>();

        for (Element iframe : iframes) {
            List<Post> rows = processIframe(iframe);
            if (!rows.isEmpty()) {
                allRows.addAll(rows);
            }
        }

        return allRows;
    }

    private List<Post> processIframe(Element iframe) {
        String iframeUrl = iframe.absUrl("src");

        if (iframeUrl.isEmpty()) {
            return List.of();
        }

        try {
            Document iframeDoc = CrawlingUtil.fetchDocument(iframeUrl);

            Elements headers = getHeaders(iframeDoc);
            if (headers.isEmpty()) {
                return List.of();
            }

            PostIdx idx = PostIdx.getPostIdxFromHeader(headers);

            if (Objects.requireNonNull(idx).isInValid()) {
                return List.of();
            }

            Elements contentElements = getContentElements(iframeDoc);

            if (!contentElements.isEmpty()) {
                log.info("TableCrawlingHandler extract rows: Found {} rows in the document in iframe : {}", contentElements.size(), iframeUrl);
            }

            return getPosts(contentElements, idx);
        } catch (IOException e) {
            log.error("Failed to process iframe: {}, Error: {}", iframeUrl, e.getMessage());
            return List.of();
        }
    }
}
