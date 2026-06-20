package com.kml.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

public class ApiRateLimitFilter extends OncePerRequestFilter {

  private static final long WINDOW_MILLIS = 60_000L;
  private static final int MAX_REQUESTS = 100;
  private static final Map<String, WindowBucket> BUCKETS = new ConcurrentHashMap<>();

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return "OPTIONS".equalsIgnoreCase(request.getMethod())
        || path.startsWith("/swagger-ui")
        || path.startsWith("/api-docs")
        || path.startsWith("/actuator/health")
        || path.startsWith("/h2-console")
        || !path.startsWith("/api/");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String clientKey = resolveClientKey(request);
    WindowBucket bucket = BUCKETS.computeIfAbsent(clientKey, key -> new WindowBucket());
    long now = Instant.now().toEpochMilli();

    synchronized (bucket) {
      if (now - bucket.windowStart >= WINDOW_MILLIS) {
        bucket.windowStart = now;
        bucket.requestCount = 0;
      }

      if (bucket.requestCount >= MAX_REQUESTS) {
        response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Too Many Requests");
        return;
      }

      bucket.requestCount++;
    }

    filterChain.doFilter(request, response);
  }

  private String resolveClientKey(HttpServletRequest request) {
    String forwardedFor = request.getHeader("X-Forwarded-For");
    if (forwardedFor != null && !forwardedFor.isBlank()) {
      return forwardedFor.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }

  private static final class WindowBucket {
    private long windowStart = Instant.now().toEpochMilli();
    private int requestCount;
  }
}
