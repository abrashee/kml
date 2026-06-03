package com.kml.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kml.auth.dto.LoginRequestDto;
import com.kml.auth.dto.RefreshTokenRequestDto;
import com.kml.auth.dto.TokenResponseDto;
import com.kml.security.jwt.JwtTokenProvider;
import com.kml.security.jwt.JwtUserDetailsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@Tag(name = "Auth")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtUserDetailsService jwtUserDetailsService;
  private final JwtTokenProvider jwtTokenProvider;

  public AuthController(
      AuthenticationManager authenticationManager,
      JwtUserDetailsService jwtUserDetailsService,
      JwtTokenProvider jwtTokenProvider) {
    this.authenticationManager = authenticationManager;
    this.jwtUserDetailsService = jwtUserDetailsService;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Operation(summary = "Login and obtain JWT tokens")
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDto requestDto) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              requestDto.getUsername(),
              requestDto.getPassword()));

      UserDetails userDetails =
          jwtUserDetailsService.loadUserByUsername(requestDto.getUsername());

      String accessToken = jwtTokenProvider.generateToken(userDetails);
      String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

      return ResponseEntity.ok(new TokenResponseDto(accessToken, refreshToken));

    } catch (Exception e) {
      e.printStackTrace(); // IMPORTANT
      return ResponseEntity.status(500).body(e.getClass() + ": " + e.getMessage());
    }
  }
  // public ResponseEntity<TokenResponseDto> login(@RequestBody @Valid LoginRequestDto requestDto) {
  //   authenticationManager.authenticate(
  //       new UsernamePasswordAuthenticationToken(
  //           requestDto.getUsername(), requestDto.getPassword()));

  //   UserDetails userDetails =
  //       jwtUserDetailsService.loadUserByUsername(requestDto.getUsername());

  //   String accessToken = jwtTokenProvider.generateToken(userDetails);
  //   String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

  //   return ResponseEntity.ok(new TokenResponseDto(accessToken, refreshToken));
  // }

  @Operation(summary = "Refresh access token using refresh token")
  @PostMapping("/refresh")
  public ResponseEntity<TokenResponseDto> refresh(
      @RequestBody @Valid RefreshTokenRequestDto requestDto) {
    String username = jwtTokenProvider.extractUsername(requestDto.getRefreshToken());
    UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

    if (!jwtTokenProvider.validateToken(requestDto.getRefreshToken(), userDetails)) {
      return ResponseEntity.status(401).build();
    }

    String newAccessToken = jwtTokenProvider.generateToken(userDetails);
    return ResponseEntity.ok(new TokenResponseDto(newAccessToken, requestDto.getRefreshToken()));
  }
}
