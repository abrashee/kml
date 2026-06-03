package com.kml.auth.dto;

public record TokenResponseDto(String accessToken, String refreshToken, String tokenType) {

  public TokenResponseDto(String accessToken, String refreshToken) {
    this(accessToken, refreshToken, "Bearer");
  }
}
