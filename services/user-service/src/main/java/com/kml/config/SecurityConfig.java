package com.kml.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kml.security.jwt.JwtAuthFilter;
import com.kml.services.common.security.jwt.JwtTokenProvider;
import com.kml.security.jwt.JwtUserDetailsService;
import com.kml.user.repository.UserRepository;
import com.kml.security.ApiRateLimitFilter;
import com.kml.security.AnomalousActivityFilter;

@Configuration
@EnableMethodSecurity
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class SecurityConfig {

  private final JwtUserDetailsService jwtUserDetailsService;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  public SecurityConfig(
      JwtUserDetailsService jwtUserDetailsService,
      JwtTokenProvider jwtTokenProvider,
      UserRepository userRepository) {
    this.jwtUserDetailsService = jwtUserDetailsService;
    this.jwtTokenProvider = jwtTokenProvider;
    this.userRepository = userRepository;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JwtAuthFilter jwtAuthFilter() {
    return new JwtAuthFilter(jwtTokenProvider, jwtUserDetailsService);
  }

  @Bean
  public ApiRateLimitFilter apiRateLimitFilter() {
    return new ApiRateLimitFilter();
  }

  @Bean
  public AnomalousActivityFilter anomalousActivityFilter() {
    return new AnomalousActivityFilter(userRepository);
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(jwtUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.disable())
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())

        // ⚡ ADD THIS: Force Spring Security to return 401 instead of 403 for unauthenticated states
        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint((request, response, authException) -> {
                response.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            })
        )

        .authorizeHttpRequests(auth -> auth
            // GROUP 1: Public Core Infrastructure & Network Preflights
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // GROUP 2: Public Business Identity Access & Sign-Up Gateways
            .requestMatchers("/api/v1/auth/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/users/register/customer").permitAll()
            .requestMatchers("/internal/v1/users/**").permitAll()

            // GROUP 3: Public Asset Pipelines & Content Delivery
            .requestMatchers(HttpMethod.GET, "/uploads/avatars/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/uploads/avatars/**").permitAll()

            // GROUP 4: Developer Sandboxes, Diagnostics & Tooling
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**").permitAll()
            .requestMatchers("/actuator/health/**").permitAll()

            // GROUP 5: Protected Customer Engine Storefront Boundaries
            .requestMatchers("/api/v1/catalog/**").authenticated()
            .requestMatchers("/api/wms/**").hasAnyRole("WORKER", "MANAGER")
            .requestMatchers("/api/ims/forecast/**").hasAnyRole("MANAGER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/v1/inventories/checkout/**").hasRole("CUSTOMER")
            .requestMatchers(HttpMethod.PATCH, "/api/v1/inventories/**").hasAnyRole("MANAGER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/v1/inventories").hasAnyRole("MANAGER", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/inventories/**").hasAnyRole("MANAGER", "ADMIN")

            // ⚡ FIX: Map the inventory search module routes directly into the security engine
            .requestMatchers("/api/v1/search/inventory/**").authenticated()

            // GROUP 6: Fallback Default Security Lock Down
            .anyRequest().authenticated()
        )
        .addFilterBefore(apiRateLimitFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(anomalousActivityFilter(), UsernamePasswordAuthenticationFilter.class);

    // Allow H2 console frames to render within the same origin safely
    http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

    return http.build();
  }
}
