package com.mvp.mvp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@EnableConfigurationProperties(JwtProperties.class)
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DaoAuthenticationProvider daoAuthenticationProvider;
    @Autowired
    private JwtProperties jwtProperties;

    public RequestAuthenticationFilter authenticationFilter() throws Exception {
        RequestAuthenticationFilter authenticationFilter
                = new RequestAuthenticationFilter(jwtProvider, objectMapper, jwtProperties);
        authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/login").anonymous().and()
                .authorizeRequests().antMatchers("/user/register").permitAll().and()
                .authorizeRequests().antMatchers("/product/get/**").permitAll().and()
                .authorizeRequests().antMatchers("/user/**").authenticated()
                .antMatchers("/product/**").authenticated()
                .and()
                .addFilterBefore(
                        authenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilter(new JwtFilter(jwtProvider, authenticationManager()));
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers("/v2/api-docs")
                .antMatchers("/swagger-resources/**").antMatchers("/swagger-ui.html")
                .antMatchers("/configuration/**").antMatchers("/webjars/**")
                .antMatchers("/public");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
