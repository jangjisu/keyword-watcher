package com.app.keywordwatcher.crawler;

import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.post.Post;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.sitekeyword.SiteKeyword;
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

        Elements rows = extractRows(doc);
        for (Element row : rows) {
            log.info("Crawling row: {}", row.text());
        }

        if (rows.isEmpty()) {
            if (nextHandler == null) {
                throw new CrawlingParseException("Unsupported document format for " + siteInfo.getUrl());
            }
            return nextHandler.handle(doc, siteInfo, date);
        }

        List<Post> datePosts = rows.stream()
                .map(el -> mapToPostIfMatchDate(el, siteInfo, date))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        List<String> keyTexts = siteInfo.getSiteKeywords().stream()
                .map(SiteKeyword::getKeyword)
                .map(Keyword::getKeyText)
                .toList();

        return datePosts.stream()
                .filter(post -> keyTexts.stream().anyMatch(post.getTitle()::contains))
                .toList();
    }

    public List<Post> handle(Site siteInfo, LocalDate date) throws IOException {
        Document doc = CrawlingUtil.fetchDocument(siteInfo.getUrl());
        log.info("Crawling document from URL: {}", siteInfo.getUrl());
        return handle(doc, siteInfo, date);
    }

    protected abstract Elements extractRows(Document doc);

    private Optional<Post> mapToPostIfMatchDate(Element element, Site siteInfo, LocalDate date) {
        Elements tds = element.select("td");
        int dateIdx = siteInfo.getCreateAtPosition();
        int titleIdx = siteInfo.getTitlePosition();

        if (tds.size() <= dateIdx || tds.size() <= titleIdx) {
            throw new CrawlingParseException("Crawling parse error on " + siteInfo.getUrl() +
                    ". Expected at least " + (Math.max(dateIdx, titleIdx) + 1) +
                    " columns, but found " + tds.size() + ".");
        }

        String title = tds.get(titleIdx).text();
        String dateText = tds.get(dateIdx).text();

        Optional<LocalDate> createAt = DateUtil.parseDate(dateText);
        if (createAt.isEmpty() || !createAt.get().equals(date)) {
            return Optional.empty();
        }

        return Optional.of(Post.createPost(title, createAt.get()));
    }
}
