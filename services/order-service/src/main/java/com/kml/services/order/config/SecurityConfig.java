package com.kml.services.order.config;

import com.kml.services.common.security.config.SharedSecurityConfigurer;
import com.kml.services.common.security.jwt.JwtTokenProvider;
import com.kml.services.common.security.jwt.JwtTokenInvalidationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@Import(com.kml.services.common.security.config.MethodSecurityConfig.class)
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtTokenProvider jwtTokenProvider,
      JwtTokenInvalidationService tokenInvalidationService) throws Exception {

    SharedSecurityConfigurer.statelessJwt(http, jwtTokenProvider, tokenInvalidationService);

    http.authorizeHttpRequests(auth -> {
      SharedSecurityConfigurer.permitCommonPublicEndpoints(auth);

      auth
          .requestMatchers(HttpMethod.GET, "/api/v1/orders/service-info").authenticated()
          .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasRole("CUSTOMER")
          .requestMatchers(HttpMethod.GET, "/api/v1/orders/**").hasAnyRole("ADMIN", "MANAGER", "WORKER", "CUSTOMER")
          .requestMatchers(HttpMethod.PATCH, "/api/v1/orders/*/items").hasRole("CUSTOMER")
          .requestMatchers(HttpMethod.PATCH, "/api/v1/orders/*/status/**").hasAnyRole("ADMIN", "MANAGER", "WORKER")
          .requestMatchers(HttpMethod.PATCH, "/api/v1/orders/*/worker/**").hasAnyRole("ADMIN", "MANAGER")
          .anyRequest().authenticated();
    });

    return http.build();
  }
}
