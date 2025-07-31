package com.app.keywordwatcher.domain.sitekeyword;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteKeywordRepository extends JpaRepository<SiteKeyword, Long> {
    List<SiteKeyword> findBySiteId(Long siteId);
}
