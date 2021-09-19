package com.mvp.mvp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvp.mvp.model.request.LoginRequest;
import com.mvp.mvp.model.security.UserDetailsImpl;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    public RequestAuthenticationFilter(JwtProvider jwtProvider, ObjectMapper objectMapper, JwtProperties jwtProperties) {
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest authRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);

            UsernamePasswordAuthenticationToken token
                    = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
            setDetails(request, token);

            return this.getAuthenticationManager().authenticate(token);
        } catch (IOException e) {
            throw new InternalAuthenticationServiceException("ERROR_MESSAGE", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) {
        String token = jwtProvider.createToken(((UserDetailsImpl) auth.getPrincipal()).getUsername());
        res.addHeader(jwtProperties.getHeaderName(), jwtProperties.getStartsWith() + token);
    }
}
