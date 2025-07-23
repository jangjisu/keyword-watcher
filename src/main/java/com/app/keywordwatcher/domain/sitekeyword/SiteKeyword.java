package com.app.keywordwatcher.domain.sitekeyword;

import com.app.keywordwatcher.domain.BaseEntity;
import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.site.Site;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SiteKeyword extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    private Keyword keyword;

    public SiteKeyword(Site site, Keyword keyword) {
        this.site = site;
        this.keyword = keyword;
    }
}
