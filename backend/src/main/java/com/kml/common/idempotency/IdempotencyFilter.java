package com.kml.common.idempotency;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class IdempotencyFilter extends OncePerRequestFilter {

  private static final long TTL_MS = 24 * 60 * 60 * 1000L;
  private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

  private final ConcurrentHashMap<String, CachedResponse> cache = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String idempotencyKey = request.getHeader(IDEMPOTENCY_KEY_HEADER);
    String method = request.getMethod();

    if (idempotencyKey == null || idempotencyKey.isBlank() || !"POST".equalsIgnoreCase(method)) {
      filterChain.doFilter(request, response);
      return;
    }

    CachedResponse cached = cache.get(idempotencyKey);
    if (cached != null && !cached.isExpired(TTL_MS)) {
      response.setStatus(cached.getStatus());
      if (cached.getContentType() != null) {
        response.setContentType(cached.getContentType());
      }
      response.getOutputStream().write(cached.getBody());
      return;
    }

    ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
    filterChain.doFilter(request, wrappedResponse);

    byte[] body = wrappedResponse.getContentAsByteArray();
    cache.put(
        idempotencyKey,
        new CachedResponse(wrappedResponse.getStatus(), body, wrappedResponse.getContentType()));

    wrappedResponse.copyBodyToResponse();
  }
}
