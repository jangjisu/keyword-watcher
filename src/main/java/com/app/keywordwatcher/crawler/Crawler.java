package com.app.keywordwatcher.crawler;

import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.post.Post;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.sitekeyword.SiteKeyword;
import org.jsoup.nodes.Document;

import java.time.LocalDate;
import java.util.List;

public abstract class Crawler {
    protected abstract boolean cannotHandle(Document doc);

    protected abstract List<Post> getDatePost(Document doc, Site siteInfo, LocalDate date);

    List<Post> getNewPosts(Document doc, Site siteInfo, LocalDate date) {
        List<Post> datePost = getDatePost(doc, siteInfo, date);

        List<String> keyTexts = siteInfo.getSiteKeywords().stream()
                .map(SiteKeyword::getKeyword)
                .map(Keyword::getKeyText)
                .toList();

        return datePost.stream()
                .filter(post -> keyTexts.stream()
                        .anyMatch(keyText -> post.getTitle().contains(keyText)))
                .toList();
    }
}
