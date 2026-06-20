package com.kml.security;

import com.kml.user.entity.User;
import com.kml.user.entity.UserAccountStatus;
import com.kml.user.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Primary
public class SecurityContextCurrentUserProvider implements CurrentUserProvider {

  private final UserRepository userRepository;

  public SecurityContextCurrentUserProvider(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      throw new IllegalStateException("No authenticated user in SecurityContext");
    }

    String username = auth.getName();

    // Fetch the MANAGED entity from the database instead of creating a new one
    return userRepository.findByUsername(username)
        .map(user -> {
          if (user.getAccountStatus() == UserAccountStatus.SUSPENDED_POTENTIAL_BOT) {
            throw new IllegalStateException("User account suspended: " + username);
          }
          return user;
        })
        .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database: " + username));
  }
}
