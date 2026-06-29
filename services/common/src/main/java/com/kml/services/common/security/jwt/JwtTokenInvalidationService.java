package com.kml.services.common.security.jwt;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenInvalidationService {

  private static final String PREFIX = "kml:jwt:blacklist:";

  private final StringRedisTemplate redisTemplate;
  private final JwtTokenProvider jwtTokenProvider;

  public JwtTokenInvalidationService(
      StringRedisTemplate redisTemplate,
      JwtTokenProvider jwtTokenProvider) {
    this.redisTemplate = redisTemplate;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  public void invalidate(String token) {
    if (token == null || token.isBlank()) {
      return;
    }

    String tokenId = jwtTokenProvider.extractTokenId(token);
    long remainingTtlMs = jwtTokenProvider.remainingTtlMs(token);

    if (tokenId == null || tokenId.isBlank() || remainingTtlMs <= 0) {
      return;
    }

    redisTemplate.opsForValue().set(key(tokenId), "revoked", Duration.ofMillis(remainingTtlMs));
  }

  public boolean isInvalidated(String token) {
    if (token == null || token.isBlank()) {
      return false;
    }

    String tokenId = jwtTokenProvider.extractTokenId(token);
    if (tokenId == null || tokenId.isBlank()) {
      return false;
    }

    return Boolean.TRUE.equals(redisTemplate.hasKey(key(tokenId)));
  }

  private String key(String tokenId) {
    return PREFIX + tokenId;
  }
}
