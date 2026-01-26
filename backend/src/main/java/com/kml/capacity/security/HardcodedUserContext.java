package com.kml.capacity.security;

import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;

public class HardcodedUserContext {
  // note: this is only for this MVP, next MVP will be implmenting proper security.
  public static User getCurrentUser() {
    return new User(
        "nameplaceholder", "usernameplaceholder", "passwordplaceholder", UserRole.ADMIN);
  }
}
