package com.app.two.factor.auth.jwt;

import com.app.two.factor.auth.user.UserEntity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetails extends User {
    private UserEntity user;

    public JwtUserDetails(UserEntity user) {
        super(user.getEmail(), user.getPassword(),
                AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_STEP1_COMPLETED"));
        this.user = user;
    }

    public Long getId() {
        return this.user.getId();
    }
}