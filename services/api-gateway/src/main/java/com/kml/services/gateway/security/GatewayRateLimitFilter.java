package com.kml.services.gateway.security;

import com.kml.services.gateway.config.GatewayRateLimitProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

public class GatewayRateLimitFilter extends OncePerRequestFilter {

    private final GatewayRateLimitProperties properties;
    private final Clock clock;
    private final Map<String, WindowBucket> buckets = new ConcurrentHashMap<>();

    public GatewayRateLimitFilter(GatewayRateLimitProperties properties) {
        this(properties, Clock.systemUTC());
    }

    GatewayRateLimitFilter(GatewayRateLimitProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
            || path.startsWith("/actuator/health")
            || "/actuator/prometheus".equals(path)
            || !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String clientKey = resolveClientKey(request);
        long now = clock.millis();
        long windowMillis = properties.getWindow().toMillis();
        WindowBucket bucket = buckets.computeIfAbsent(clientKey, ignored -> new WindowBucket(now));

        synchronized (bucket) {
            if (now - bucket.windowStartMillis >= windowMillis) {
                bucket.windowStartMillis = now;
                bucket.requestCount = 0;
            }

            if (bucket.requestCount >= properties.getCapacity()) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("text/plain");
                response.getWriter().write("Too Many Requests");
                response.flushBuffer();
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
        private long windowStartMillis;
        private int requestCount;

        private WindowBucket(long windowStartMillis) {
            this.windowStartMillis = windowStartMillis;
        }
    }
}
