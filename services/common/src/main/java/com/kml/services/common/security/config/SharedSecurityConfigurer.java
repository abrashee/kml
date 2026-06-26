package com.kml.services.common.security.config;

import com.kml.services.common.security.jwt.JwtTokenProvider;
import com.kml.services.common.security.jwt.SharedJwtAuthFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public final class SharedSecurityConfigurer {

  private SharedSecurityConfigurer() {}

  public static HttpSecurity statelessJwt(HttpSecurity http, JwtTokenProvider jwtTokenProvider)
      throws Exception {
    return http
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> ex.authenticationEntryPoint(
            (request, response, authException) ->
                response.sendError(401, "Unauthorized")))
        .addFilterBefore(
            new SharedJwtAuthFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class);
  }

  public static void permitCommonPublicEndpoints(
      org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer<HttpSecurity>
          .AuthorizationManagerRequestMatcherRegistry auth) {
    auth
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .requestMatchers("/actuator/health/**").permitAll();
  }
}
