package com.kml.capacity.security;

import com.kml.domain.user.User;

public interface CurrentUserProvider {
  User getCurrentUser();
}
