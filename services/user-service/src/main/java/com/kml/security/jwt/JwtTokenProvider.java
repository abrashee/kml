package com.kml.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

  private final JwtProperties jwtProperties;

  public JwtTokenProvider(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
  }

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(UserDetails userDetails) {
  long now = System.currentTimeMillis();

  String role = userDetails.getAuthorities()
      .stream()
      .findFirst()
      .map(a -> a.getAuthority())
      .orElse("ROLE_CUSTOMER");

  return Jwts.builder()
      .subject(userDetails.getUsername())
      .claim("role", role.replace("ROLE_", ""))   // CLEAN ROLE
      .issuedAt(new Date(now))
      .expiration(new Date(now + jwtProperties.getExpirationMs()))
      .signWith(getSigningKey())
      .compact();
}

  public String generateRefreshToken(UserDetails userDetails) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .subject(userDetails.getUsername())
        .issuedAt(new Date(now))
        .expiration(new Date(now + jwtProperties.getRefreshExpirationMs()))
        .claim("type", "refresh")
        .signWith(getSigningKey())
        .compact();
  }

  public String extractUsername(String token) {
    return parseClaims(token).getSubject();
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    try {
      String username = extractUsername(token);
      return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isTokenExpired(String token) {
    return parseClaims(token).getExpiration().before(new Date());
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
