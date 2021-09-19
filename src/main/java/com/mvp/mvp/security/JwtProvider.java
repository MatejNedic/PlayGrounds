package com.mvp.mvp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;

@Component
public class JwtProvider {

    private JwtProperties jwtProperties;
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public JwtProvider(JwtProperties jwtProperties,
                       UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtProperties = jwtProperties;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    public String createToken(String username) {

        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();
        Date validity = new Date(
                now.getTime() + jwtProperties.getValidityInMilliseconds());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsServiceImpl
                .loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                new ArrayList<>());
    }

    public Authentication getAuthenticationByUsername(String username) {
        UserDetails userDetails = userDetailsServiceImpl
                .loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                new ArrayList<>());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(jwtProperties.getHeaderName());
        if (bearerToken != null
                && bearerToken.startsWith(jwtProperties.getStartsWith())) {
            return bearerToken.substring(jwtProperties.getStartsWith().length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnsupportedOperationException("Jwt token is not valid", e);
        }
    }
}
