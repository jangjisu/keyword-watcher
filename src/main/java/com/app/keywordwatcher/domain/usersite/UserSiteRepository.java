package com.app.keywordwatcher.domain.usersite;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSiteRepository extends JpaRepository<UserSite, Long> {
    List<UserSite> findByUserId(Long userId);
}
