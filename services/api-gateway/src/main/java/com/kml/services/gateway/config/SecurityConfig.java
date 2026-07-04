package com.kml.services.gateway.config;

import com.kml.services.common.security.config.SharedSecurityConfigurer;
import com.kml.services.common.security.jwt.JwtTokenInvalidationService;
import com.kml.services.common.security.jwt.JwtTokenProvider;
import com.kml.services.gateway.security.GatewayRateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenInvalidationService tokenInvalidationService;
    private final GatewayRateLimitFilter gatewayRateLimitFilter;

    public SecurityConfig(
        JwtTokenProvider jwtTokenProvider,
        JwtTokenInvalidationService tokenInvalidationService,
        GatewayRateLimitFilter gatewayRateLimitFilter) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenInvalidationService = tokenInvalidationService;
        this.gatewayRateLimitFilter = gatewayRateLimitFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        SharedSecurityConfigurer.statelessJwt(http, jwtTokenProvider, tokenInvalidationService);
        http.addFilterBefore(gatewayRateLimitFilter, SecurityContextHolderFilter.class);

        http.authorizeHttpRequests(auth -> {
            SharedSecurityConfigurer.permitCommonPublicEndpoints(auth);

            auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
            auth.requestMatchers(
                "/api/v1/auth/**",
                "/api/v1/users/register/customer",
                "/uploads/**"
            ).permitAll();

            auth.anyRequest().authenticated();
        });

        return http.build();
    }
}
