package com.kml.auth.dto;

public record TokenResponseDto(String accessToken, String tokenType) {

  public TokenResponseDto(String accessToken) {
    this(accessToken, "Bearer");
  }
}
