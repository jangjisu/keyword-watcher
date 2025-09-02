package com.app.keywordwatcher.domain.site;

import com.app.keywordwatcher.domain.BaseEntity;
import com.app.keywordwatcher.web.service.user.response.SiteResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Site extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private Site(String url) {
        this.url = url;
    }

    public static Site create(String url) {
        return new Site(url);
    }

    public SiteResponse toResponse() {
        return SiteResponse.builder()
                .url(this.url)
                .build();
    }
}
