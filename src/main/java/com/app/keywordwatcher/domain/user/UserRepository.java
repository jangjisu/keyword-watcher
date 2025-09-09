package com.app.keywordwatcher.domain.user;

import com.app.keywordwatcher.domain.site.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);

    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN FETCH u.userKeywords uk " +
            "JOIN FETCH uk.keyword " +
            "JOIN u.userSites us " +
            "WHERE us.site = :site")
    List<User> findUsersBySiteWithKeywords(@Param("site") Site site);
}
