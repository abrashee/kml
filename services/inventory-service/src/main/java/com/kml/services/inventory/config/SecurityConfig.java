package com.kml.services.inventory.config;

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
          .requestMatchers(HttpMethod.GET, "/api/v1/products/**").authenticated()
          .requestMatchers(HttpMethod.GET, "/api/v1/inventories/service-info").authenticated()
          .requestMatchers(HttpMethod.GET, "/api/v1/inventories/**").hasAnyRole("ADMIN", "MANAGER", "WORKER")
          .requestMatchers(HttpMethod.POST, "/api/v1/inventories").hasAnyRole("ADMIN", "MANAGER")
          .requestMatchers(HttpMethod.PATCH, "/api/v1/inventories/**").hasAnyRole("ADMIN", "MANAGER")
          .requestMatchers(HttpMethod.DELETE, "/api/v1/inventories/**").hasRole("ADMIN")
          .anyRequest().authenticated();
    });

    return http.build();
  }
}
