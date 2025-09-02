package com.app.keywordwatcher.crawler;

import com.app.keywordwatcher.domain.post.Post;
import com.app.keywordwatcher.domain.post.PostIdx;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Objects;

@Slf4j
public class TableCrawlingHandler extends CrawlingHandler {
    public TableCrawlingHandler(CrawlingHandler nextHandler) {
        super(nextHandler);
    }

    @Override
    protected List<Post> extractPosts(Document doc) {
        Elements headers = getHeaders(doc);
        if (headers.isEmpty()) {
            return List.of();
        }

        PostIdx idx = PostIdx.getPostIdxFromHeader(headers);

        if (Objects.requireNonNull(idx).isInValid()) {
            return List.of();
        }

        Elements contentElements = getContentElements(doc);

        if (contentElements.isEmpty()) {
            return List.of();
        }

        return getPosts(contentElements, idx);
    }
}