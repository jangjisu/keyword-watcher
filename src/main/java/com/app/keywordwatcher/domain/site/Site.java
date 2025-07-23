package com.app.keywordwatcher.domain.site;

import com.app.keywordwatcher.domain.BaseEntity;
import com.app.keywordwatcher.domain.sitekeyword.SiteKeyword;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Site extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private int titlePosition;

    private int createAtPosition;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List<SiteKeyword> siteKeywords = new ArrayList<>();

    private Site(String url, int titlePosition, int createAtPosition) {
        this.url = url;
        this.titlePosition = titlePosition;
        this.createAtPosition = createAtPosition;
    }

    public static Site create(String url, int titlePosition, int createAtPosition) {
        return new Site(url, titlePosition, createAtPosition);
    }


}
