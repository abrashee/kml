package com.kml.services.gateway.config;

import com.kml.services.common.security.config.SharedSecurityConfigurer;
import com.kml.services.common.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        SharedSecurityConfigurer.statelessJwt(http, jwtTokenProvider);

        http.authorizeHttpRequests(auth -> {
            SharedSecurityConfigurer.permitCommonPublicEndpoints(auth);

auth.requestMatchers(
    "/api/v1/auth/**",
    "/uploads/**"
).permitAll();

auth.anyRequest().authenticated();
        });

        return http.build();
    }
}
