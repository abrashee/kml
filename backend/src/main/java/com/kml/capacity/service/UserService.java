package com.kml.capacity.service;

import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;

public interface UserService {

  User createUser(String name, String username, String password, UserRole userRole);

  boolean existsByUsername(String username);
}
