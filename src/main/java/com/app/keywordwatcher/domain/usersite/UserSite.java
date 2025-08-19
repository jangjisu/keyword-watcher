package com.app.keywordwatcher.domain.usersite;

import com.app.keywordwatcher.domain.BaseEntity;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserSite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Site site;

    protected UserSite(User user, Site site) {
        this.user = user;
        this.site = site;
    }

    public static UserSite create(User user, Site site) {
        return new UserSite(user, site);
    }
}
