package com.kml.capacity.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kml.capacity.service.UserService;
import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;
import com.kml.infra.UserRepository;

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
  public User createUser(String name, String username, String password, UserRole userRole) {

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
    return userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByUsername(String username) {
    return this.userRepository.existsByUsername(username);
  }
}
