package com.kml.capacity.service.serviceImplementation;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kml.capacity.service.UserService;
import com.kml.domain.user.User;
import com.kml.domain.user.UserRole;
import com.kml.infra.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImplementation implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public User createUser(String name, String username, String password, String userRoleApi) {

    UserRole userRole;
    try {
      userRole = UserRole.valueOf(userRoleApi.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Invalid User Role. Allow values: ADMIN, MANAGER, USER , CUSTOMER");
    }

    boolean userExists = existsByUsername(username);
    if (userExists) {
      throw new IllegalArgumentException("Username already exits");
    }

    String hashedPassword = passwordEncoder.encode(password);

    User user = new User(name, username, hashedPassword, userRole);
    return this.userRepository.save(user);
  }

  @Override
  public boolean existsByUsername(String username) {
    return this.userRepository.existsByUsername(username);
  }
}
