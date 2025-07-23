package com.app.keywordwatcher.crawler;

import com.app.keywordwatcher.domain.post.Post;
import com.app.keywordwatcher.domain.site.Site;
import org.jsoup.nodes.Document;

import java.util.List;

public abstract class Crawler {
    public Post getNewPost(Document doc, Site siteInfo) {
        if (cannotHandle(doc)) {
            return Post.emptyPost();
        }
        List<Post> posts = getPosts(doc);

        return getNewPost(posts, siteInfo);
    }

    protected abstract boolean cannotHandle(Document doc);

    protected abstract List<Post> getPosts(Document doc);

    protected abstract Post getNewPost(List<Post> posts, Site siteInfo);
}
