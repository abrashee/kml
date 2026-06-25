package com.kml.auth.controller;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kml.auth.dto.LoginRequestDto;
import com.kml.auth.dto.RefreshTokenRequestDto;
import com.kml.auth.dto.TokenResponseDto;
import com.kml.services.common.security.jwt.JwtTokenProvider;
import com.kml.security.jwt.JwtUserDetailsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private static final String REFRESH_COOKIE_NAME = "kml_refresh_token";

  private final AuthenticationManager authenticationManager;
  private final JwtUserDetailsService jwtUserDetailsService;
  private final JwtTokenProvider jwtTokenProvider;
  private final boolean refreshCookieSecure;

  public AuthController(
      AuthenticationManager authenticationManager,
      JwtUserDetailsService jwtUserDetailsService,
      JwtTokenProvider jwtTokenProvider,
      @Value("${kml.auth.refresh-cookie-secure:false}") boolean refreshCookieSecure) {
    this.authenticationManager = authenticationManager;
    this.jwtUserDetailsService = jwtUserDetailsService;
    this.jwtTokenProvider = jwtTokenProvider;
    this.refreshCookieSecure = refreshCookieSecure;
  }

  @Operation(summary = "Login and obtain JWT tokens")
  @PostMapping("/login")
  public ResponseEntity<TokenResponseDto> login(
      @RequestBody @Valid LoginRequestDto requestDto,
      HttpServletResponse response) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            requestDto.getUsername(),
            requestDto.getPassword()));

    UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(requestDto.getUsername());
    String accessToken = jwtTokenProvider.generateToken(userDetails);
    String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie(refreshToken).toString());
    return ResponseEntity.ok(new TokenResponseDto(accessToken));
  }

  @Operation(summary = "Refresh access token using refresh token cookie")
  @PostMapping("/refresh")
  public ResponseEntity<TokenResponseDto> refresh(
      @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken,
      @RequestBody(required = false) RefreshTokenRequestDto ignored,
      HttpServletResponse response) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return ResponseEntity.status(401).build();
    }

    try {
      String username = jwtTokenProvider.extractUsername(refreshToken);
      UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

      if (!jwtTokenProvider.validateRefreshToken(refreshToken, userDetails)) {
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString());
        return ResponseEntity.status(401).build();
      }

      String newAccessToken = jwtTokenProvider.generateToken(userDetails);
      String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
      response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie(newRefreshToken).toString());
      return ResponseEntity.ok(new TokenResponseDto(newAccessToken));
    } catch (Exception ex) {
      response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString());
      return ResponseEntity.status(401).build();
    }
  }


  @Operation(summary = "Logout and clear refresh token cookie")
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString());
    return ResponseEntity.noContent().build();
  }

  private ResponseCookie refreshCookie(String refreshToken) {
    return ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
        .httpOnly(true)
        .secure(refreshCookieSecure)
        .sameSite("Lax")
        .path("/api/v1/auth")
        .maxAge(Duration.ofDays(7))
        .build();
  }

  private ResponseCookie clearRefreshCookie() {
    return ResponseCookie.from(REFRESH_COOKIE_NAME, "")
        .httpOnly(true)
        .secure(refreshCookieSecure)
        .sameSite("Lax")
        .path("/api/v1/auth")
        .maxAge(Duration.ZERO)
        .build();
  }
}
