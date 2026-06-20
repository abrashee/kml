package com.kml.security;

import com.kml.user.entity.UserAccountStatus;
import com.kml.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Optimized for Microservices deployment.
 * NOTE: For multi-instance production environments, REQUEST_TIMESTAMPS maps
 * should be replaced with a Redis distributed sliding window script.
 */
public class AnomalousActivityFilter extends OncePerRequestFilter {

  private static final int THRESHOLD = 50;
  private static final long WINDOW_MILLIS = 60_000L;

  // Localized state container — to be shared via cache layer in multi-node orchestration
  private static final Map<String, Deque<Long>> REQUEST_TIMESTAMPS = new ConcurrentHashMap<>();

  private final UserRepository userRepository;

  public AnomalousActivityFilter(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String method = request.getMethod();
    String path = request.getRequestURI();
    return !(
        ("POST".equals(method) && path.startsWith("/api/v1/orders"))
            || ("POST".equals(method) && path.startsWith("/api/v1/inventories/checkout"))
            || ("PUT".equals(method) && path.startsWith("/api/v1/orders"))
    );
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      filterChain.doFilter(request, response);
      return;
    }

    String username = authentication.getName();
    long now = Instant.now().toEpochMilli();
    Deque<Long> timestamps = REQUEST_TIMESTAMPS.computeIfAbsent(username, key -> new ArrayDeque<>());

    boolean limitsExceeded = false;
    synchronized (timestamps) {
      while (!timestamps.isEmpty() && now - timestamps.peekFirst() > WINDOW_MILLIS) {
        timestamps.removeFirst();
      }
      timestamps.addLast(now);

      if (timestamps.size() > THRESHOLD) {
        limitsExceeded = true;
      }
    }

    if (limitsExceeded) {
      suspendAccount(username);
      throw new TooManyRequestsException("Anomalous threat request rate exceeded for account: " + username);
    }

    filterChain.doFilter(request, response);
  }

  private void suspendAccount(String username) {
    userRepository.findByUsername(username).ifPresent(user -> {
      if (user.getAccountStatus() != UserAccountStatus.SUSPENDED_POTENTIAL_BOT) {
        user.setAccountStatus(UserAccountStatus.SUSPENDED_POTENTIAL_BOT);
        userRepository.saveAndFlush(user);

        // Asynchronous Notification Broadcast out to the Message Topology (Kafka / RabbitMQ)
        // brokerTemplate.convertAndSend("kml.security.events", "account.suspended", new AccountSuspendedEvent(username));
      }
    });
  }
}
