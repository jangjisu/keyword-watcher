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

    protected boolean canHandle(Document doc) {
        if (doc == null) {
            return false;
        }

        List<Post> posts = extractPosts(doc);
        if (posts.isEmpty()) {
            if (nextHandler == null) {
                return false;
            }
            return nextHandler.canHandle(doc);
        }

        return true;
    }

    public boolean canHandle(String siteUrl) {
        try {
            Document doc = CrawlingUtil.fetchDocument(siteUrl);
            log.info("Crawling Test URL: {}", siteUrl);
            return canHandle(doc);
        } catch (IOException e) {
            log.warn("Failed to fetch document for URL: {}, Error: {}", siteUrl, e.getMessage());
            return false;
        }
    }

    protected List<Post> handle(Document doc) {
        if (doc == null) {
            throw new CrawlingParseException("Document cannot be null");
        }

        List<Post> posts = extractPosts(doc);
        for (Post post : posts) {
            log.info("Crawling row: {}", post.toString());
        }

        if (posts.isEmpty()) {
            if (nextHandler == null) {
                throw new CrawlingParseException("No More Handler");
            }
            return nextHandler.handle(doc);
        }

        return posts;
    }

    public List<Post> handle(Site siteInfo) {
        try {
            Document doc = CrawlingUtil.fetchDocument(siteInfo.getUrl());
            log.info("Crawling document from URL: {}", siteInfo.getUrl());
            return handle(doc);
        } catch (IOException e) {
            throw new CrawlingParseException("cannot Load document for " + siteInfo.getUrl());
        } catch (CrawlingParseException e) {
            if ("No More Handler".equals(e.getMessage())) {
                throw new CrawlingParseException("unsupported document for " + siteInfo.getUrl());
            }

            throw e;
        }
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
