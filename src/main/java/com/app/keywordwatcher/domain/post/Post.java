package com.app.keywordwatcher.domain.post;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    private String title;

    private LocalDateTime createAt;

    public static Post emptyPost() {
        return new Post();
    }
}
