package com.kml.capacity.service;

import com.kml.capacity.dto.user.UserResponseDto;
import com.kml.domain.user.UserRole;

public interface UserService {

  UserResponseDto createUser(String name, String username, String password, UserRole userRole);

  boolean existsByUsername(String username);
}
