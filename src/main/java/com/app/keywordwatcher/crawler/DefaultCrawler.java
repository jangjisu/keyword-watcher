package com.app.keywordwatcher.crawler;

import com.app.keywordwatcher.domain.post.Post;
import com.app.keywordwatcher.domain.site.Site;
import org.jsoup.nodes.Document;

import java.util.List;

public class DefaultCrawler extends Crawler {
    @Override
    protected boolean cannotHandle(Document doc) {
        if (doc == null) {
            return true;
        }

        return doc.select("table tbody tr").isEmpty();
    }

    @Override
    protected List<Post> getPosts(Document doc) {
        return List.of();
    }

    @Override
    protected Post getNewPost(List<Post> posts, Site siteInfo) {
        return null;
    }
}
