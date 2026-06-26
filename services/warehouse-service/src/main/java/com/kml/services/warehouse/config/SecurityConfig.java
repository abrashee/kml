package com.kml.services.warehouse.config;

import com.kml.services.common.security.config.SharedSecurityConfigurer;
import com.kml.services.common.security.jwt.JwtTokenProvider;
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
      JwtTokenProvider jwtTokenProvider) throws Exception {

    SharedSecurityConfigurer.statelessJwt(http, jwtTokenProvider);

    http.authorizeHttpRequests(auth -> {
      SharedSecurityConfigurer.permitCommonPublicEndpoints(auth);

      auth
          .requestMatchers(HttpMethod.GET, "/api/v1/warehouses/service-info").authenticated()
          .requestMatchers(HttpMethod.GET, "/api/v1/warehouses/**").hasAnyRole("ADMIN", "MANAGER", "WORKER")
          .requestMatchers(HttpMethod.POST, "/api/v1/warehouses").hasRole("ADMIN")
          .requestMatchers(HttpMethod.POST, "/api/v1/warehouses/*/storage-units").hasAnyRole("ADMIN", "MANAGER")
          .anyRequest().authenticated();
    });

    return http.build();
  }
}
