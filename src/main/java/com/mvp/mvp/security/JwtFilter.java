package com.mvp.mvp.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends BasicAuthenticationFilter {

    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider, AuthenticationManager authManager) {
        super(authManager);
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtProvider.resolveToken(httpServletRequest);
        try {
            if (token != null && jwtProvider.validateToken(token)) {
                Authentication auth = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Throwable ex) {
            SecurityContextHolder.clearContext();
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                    ex.getMessage());
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
