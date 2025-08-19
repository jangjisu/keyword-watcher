package com.app.keywordwatcher.domain.userkeyword;

import com.app.keywordwatcher.domain.BaseEntity;
import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserKeyword extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Keyword keyword;

    protected UserKeyword(User user, Keyword keyword) {
        this.user = user;
        this.keyword = keyword;
    }

    public static UserKeyword create(User user, Keyword keyword) {
        return new UserKeyword(user, keyword);
    }
}
