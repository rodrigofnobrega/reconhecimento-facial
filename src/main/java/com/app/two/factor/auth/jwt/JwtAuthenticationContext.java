package com.app.two.factor.auth.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class JwtAuthenticationContext {
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static Long getId() {
        JwtUserDetails jwtUserDetails = validatePrincipal();

        if (jwtUserDetails != null) {
            return jwtUserDetails.getId();
        }

        return null;
    }

    public static String getEmail() {
        JwtUserDetails jwtUserDetails = validatePrincipal();

        if (jwtUserDetails != null) {
            return jwtUserDetails.getUsername();
        }

        return null;
    }

    public static Collection<GrantedAuthority> getAuthoritie() {
        JwtUserDetails jwtUserDetails = validatePrincipal();

        if (jwtUserDetails != null) {
            return jwtUserDetails.getAuthorities();
        }

        return null;
    }

    public static JwtUserDetails validatePrincipal() {
        Authentication authentication = getAuthentication();

        Object principal = authentication.getPrincipal();

        if (principal instanceof JwtUserDetails) {
            return (JwtUserDetails) principal;
        }

        return null;
    }
}