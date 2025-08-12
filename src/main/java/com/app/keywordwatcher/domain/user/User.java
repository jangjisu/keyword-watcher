package com.app.keywordwatcher.domain.user;

import com.app.keywordwatcher.domain.BaseEntity;
import com.app.keywordwatcher.web.controller.auth.request.SignupRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NullMarked
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
