package com.kml.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // MVP01 (Option B): Dev auth using HTTP Basic with in-memory users.
  // MVP02: replace with JWT + refresh tokens + RBAC.
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.POST, "/api/v1/users")
                    .permitAll()
                    .requestMatchers("/h2-console/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .httpBasic(Customizer.withDefaults());

    http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder encoder) {
    UserDetails admin =
        User.withUsername("dev-admin")
            .password(encoder.encode("dev-password"))
            .roles("ADMIN")
            .build();

    UserDetails manager =
        User.withUsername("dev-manager")
            .password(encoder.encode("dev-password"))
            .roles("MANAGER")
            .build();

    UserDetails user =
        User.withUsername("dev-user")
            .password(encoder.encode("dev-password"))
            .roles("USER")
            .build();

    UserDetails customer =
        User.withUsername("dev-customer")
            .password(encoder.encode("dev-password"))
            .roles("CUSTOMER")
            .build();

    return new InMemoryUserDetailsManager(admin, manager, user, customer);
  }
}
