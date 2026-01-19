package com.app.keywordwatcher.domain.user;

import com.app.keywordwatcher.domain.BaseEntity;
import com.app.keywordwatcher.domain.keyword.Keyword;
import com.app.keywordwatcher.domain.site.Site;
import com.app.keywordwatcher.domain.userkeyword.UserKeyword;
import com.app.keywordwatcher.domain.usersite.UserSite;
import com.app.keywordwatcher.web.controller.auth.request.SignupRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSite> userSites = new ArrayList<>();

    public void addUserSite(Site site) {
        boolean isExist = this.userSites.stream()
                .anyMatch(userSite -> userSite.getSite().equals(site));

        if (!isExist) {
            UserSite userSite = UserSite.create(this, site);
            this.userSites.add(userSite);
        }
    }

    public void removeUserSite(Site site) {
        this.userSites.removeIf(userSite ->
                userSite.getSite().equals(site));
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserKeyword> userKeywords = new ArrayList<>();

    public void addUserKeyword(Keyword keyword) {
        boolean isExist = this.userKeywords.stream()
                .anyMatch(userKeyword -> userKeyword.getKeyword().equals(keyword));

        if (!isExist) {
            UserKeyword userKeyword = UserKeyword.create(this, keyword);
            this.userKeywords.add(userKeyword);
        }
    }

    public void removeUserKeyword(Keyword keyword) {
        this.userKeywords.removeIf(userKeyword ->
                userKeyword.getKeyword().equals(keyword));
    }

    public static User create(String userId, String email, String password, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.userId = userId;
        user.email = email;
        user.password = Objects.requireNonNull(passwordEncoder.encode(password));
        user.role = Role.USER;
        return user;
    }

    public static User create(SignupRequest request, PasswordEncoder passwordEncoder) {
        return User.create(request.getUserId(), request.getEmail(), request.getPassword(), passwordEncoder);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return userId;
    }


    public enum Role {
        USER, ADMIN
    }
}
