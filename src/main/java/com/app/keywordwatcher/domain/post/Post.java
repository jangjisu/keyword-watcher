package com.app.keywordwatcher.domain.post;


import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Getter
@EqualsAndHashCode
public class Post {
    private String title;

    private LocalDate createAt;

    public static Post emptyPost() {
        return new Post();
    }

    public static Post createPost(String title, LocalDate createAt) {
        return new Post(title, createAt);
    }
}
