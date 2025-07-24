package com.app.keywordwatcher.crawler;

import com.app.keywordwatcher.domain.post.Post;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.exception.CrawlingParseException;
import com.app.keywordwatcher.util.DateUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DefaultCrawler extends Crawler {
    @Override
    protected List<Post> getDatePost(Document doc, Site siteInfo, LocalDate date) {
        return doc.select("table tbody tr").stream()
                .map(element -> mapToPostIfMatchDate(element, siteInfo, date))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<Post> mapToPostIfMatchDate(Element element, Site siteInfo, LocalDate date) {
        Elements tds = element.select("td");
        int dateIdx = siteInfo.getCreateAtPosition();

        if (tds.size() <= dateIdx) {
            throw new CrawlingParseException("Crawling parse error on " + siteInfo.getUrl() + ". Expected at least " + (dateIdx + 1) + " columns, but found " + tds.size() + ".");
        }

        LocalDate createdAt = DateUtil.parseDate(tds.get(dateIdx).text());
        if (!createdAt.equals(date)) {
            return Optional.empty();
        }

        return Optional.of(convertToPost(siteInfo, tds));
    }

    private Post convertToPost(Site siteInfo, Elements tds) {
        return Post.createPost(
                tds.get(siteInfo.getTitlePosition()).text(),
                DateUtil.parseDate(tds.get(siteInfo.getCreateAtPosition()).text())
        );
    }

    @Override
    protected boolean cannotHandle(Document doc) {
        if (doc == null) {
            return true;
        }

        return doc.select("table tbody tr").isEmpty();
    }
}
