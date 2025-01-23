package com.app.two.factor.auth.infra.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService detailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("Processando requisição: [{} {}]", request.getMethod(), request.getRequestURI());

        final String token = getJwtFromRequest(request);

        if (token == null) {
            log.info("JWT Token está nulo, vazio ou não iniciado com 'Bearer '.");
        } else if (!JwtUtils.isTokenValid(token)) {
            log.warn("JWT Token está inválido ou expirado.");
        } else {
            String username = JwtUtils.getUsernameFromToken(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                toAuthentication(request, username);
            }
        }

        filterChain.doFilter(request, response);

        log.info("Response content type: {}", response.getContentType());
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        final String AUTH_HEADER = JwtUtils.JWT_AUTHORIZATION;
        final String BEARER_PREFIX = JwtUtils.JWT_BEARER;
        final String header = request.getHeader(AUTH_HEADER);

        log.info("JWT Token: {}", header);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void toAuthentication(HttpServletRequest request, String username) {
        UserDetails userDetails = detailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken
                .authenticated(userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}