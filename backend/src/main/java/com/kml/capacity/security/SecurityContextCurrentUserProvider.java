package com.kml.capacity.security;

import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;
import java.util.Locale;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextCurrentUserProvider implements CurrentUserProvider {

  @Override
  public User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      throw new IllegalStateException("No authenticated user in SecurityContext");
    }

    String username = auth.getName();
    UserRole role = resolveRole(auth);

    // This is a only "current principal" representation, not necessarily a persisted user.
    return new User("dev-principal", username, "N/A", role);
  }

  private UserRole resolveRole(Authentication auth) {
    for (GrantedAuthority authority : auth.getAuthorities()) {
      String a = authority.getAuthority();
      if (a != null && a.startsWith("ROLE_")) {
        String roleName = a.substring("ROLE_".length()).toUpperCase(Locale.ROOT);
        return UserRole.valueOf(roleName);
      }
    }
    return UserRole.USER;
  }
}
