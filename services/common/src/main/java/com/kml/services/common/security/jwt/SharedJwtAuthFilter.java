package com.kml.services.common.security.jwt;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SharedJwtAuthFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(SharedJwtAuthFilter.class);

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtTokenInvalidationService tokenInvalidationService;

  public SharedJwtAuthFilter(
      JwtTokenProvider jwtTokenProvider,
      JwtTokenInvalidationService tokenInvalidationService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.tokenInvalidationService = tokenInvalidationService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      try {
        if (tokenInvalidationService.isInvalidated(token)) {
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token revoked");
          return;
        }

        JwtAuthenticatedUser user = jwtTokenProvider.extractAuthenticatedUser(token);
        if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(user, null, user.authorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      } catch (Exception e) {
        logger.debug("Invalid JWT token: {}", e.getMessage());
      }
    }

    filterChain.doFilter(request, response);
  }
}
