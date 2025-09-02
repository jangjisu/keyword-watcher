package com.app.keywordwatcher.crawler;

import com.app.keywordwatcher.domain.post.Post;
import com.app.keywordwatcher.domain.post.PostIdx;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.exception.CrawlingParseException;
import com.app.keywordwatcher.util.CrawlingUtil;
import com.app.keywordwatcher.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public abstract class CrawlingHandler {
    protected final CrawlingHandler nextHandler;

    protected List<Post> handle(Document doc, Site siteInfo, LocalDate date) {
        if (doc == null) {
            throw new CrawlingParseException("Document cannot be null");
        }

        List<Post> posts = extractPosts(doc);
        for (Post post : posts) {
            log.info("Crawling row: {}", post.toString());
        }

        if (posts.isEmpty()) {
            if (nextHandler == null) {
                throw new CrawlingParseException("Unsupported document format for " + siteInfo.getUrl());
            }
            return nextHandler.handle(doc, siteInfo, date);
        }

        return posts.stream()
                .filter(it -> it.getCreateAt().equals(date))
                .toList();
    }

    public List<Post> handle(Site siteInfo, LocalDate date) throws IOException {
        Document doc = CrawlingUtil.fetchDocument(siteInfo.getUrl());
        log.info("Crawling document from URL: {}", siteInfo.getUrl());
        return handle(doc, siteInfo, date);
    }

    protected abstract List<Post> extractPosts(Document doc);

    protected List<Post> getPosts(Elements contentElement, PostIdx idx) {
        List<Post> posts = new ArrayList<>();

        for (Element content : contentElement) {
            Elements tds = content.select("td");

            String title = tds.get(idx.getTitleIdx()).text();
            String dateText = tds.get(idx.getDateIdx()).text();

            Optional<LocalDate> createAt = DateUtil.parseDate(dateText);

            createAt.ifPresent(localDate -> posts.add(Post.createPost(title, localDate)));
        }

        return posts;
    }

    protected static Elements getHeaders(Document doc) {
        return doc.select("table thead tr th");
    }

    protected static Elements getContentElements(Document doc) {
        return doc.select("table tbody tr");
    }
}
