package com.kml.capacity.service.impl;

import com.kml.capacity.dto.user.UserResponseDto;
import com.kml.capacity.mapper.UserMapper;
import com.kml.capacity.service.UserService;
import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;
import com.kml.infra.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public UserResponseDto createUser(
      String name, String username, String password, UserRole userRole) {

    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name is required");
    }
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Username is required");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Password is required");
    }
    if (userRole == null) {
      throw new IllegalArgumentException("User role is required");
    }

    if (existsByUsername(username)) {
      throw new IllegalArgumentException("Username already exists");
    }

    String hashedPassword = passwordEncoder.encode(password);

    User user = new User(name, username, hashedPassword, userRole);
    User savedUser = userRepository.save(user);

    return UserMapper.toDto(savedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }
}
