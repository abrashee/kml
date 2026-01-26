package com.kml.capacity.service;

import com.kml.domain.user.User;

public interface UserService {

  User createUser(String name, String username, String password, String userRole);

  boolean existsByUsername(String username);
}
