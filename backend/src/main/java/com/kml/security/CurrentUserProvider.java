package com.kml.security;

import com.kml.user.entity.User;

public interface CurrentUserProvider {
  User getCurrentUser();
}
