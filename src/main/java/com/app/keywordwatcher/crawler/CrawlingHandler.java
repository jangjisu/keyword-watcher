package com.app.keywordwatcher.crawler;

import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.post.Post;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.sitekeyword.SiteKeyword;
import com.app.keywordwatcher.exception.CrawlingParseException;
import com.app.keywordwatcher.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class CrawlingHandler {
    protected final CrawlingHandler nextHandler;

    public List<Post> handle(Document doc, Site siteInfo, LocalDate date) {
        if (!supports(doc)) {
            if (nextHandler == null) {
                throw new CrawlingParseException("Unsupported document format for " + siteInfo.getUrl());
            }
            return nextHandler.handle(doc, siteInfo, date);
        }

        List<Element> rows = extractRows(doc); // 추출 포인트만 서브 클래스에서 변경
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

    protected abstract boolean supports(Document doc);

    protected abstract List<Element> extractRows(Document doc);

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
        LocalDate createdAt = DateUtil.parseDate(tds.get(dateIdx).text());

        if (!createdAt.equals(date)) return Optional.empty();

        return Optional.of(Post.createPost(title, createdAt));
    }
}

