package com.app.keywordwatcher.domain.userkeyword;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
    List<UserKeyword> findByUserId(Long userId);
}
