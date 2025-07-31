package com.app.keywordwatcher.domain.keyword;

import com.app.keywordwatcher.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper = false)
public class Keyword extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyText;

    private Keyword(String keyText) {
        this.keyText = keyText;
    }

    public static Keyword create(String keyText) {
        return new Keyword(keyText);
    }
}
