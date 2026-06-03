package com.kml.user.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kml.user.dto.UserResponseDto;
import com.kml.user.entity.User;
import com.kml.user.entity.UserRole;
import com.kml.user.mapper.UserMapper;
import com.kml.user.repository.UserRepository;

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

  @Override
  @Transactional(readOnly = true)
  public UserResponseDto getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User not found"));
    return UserMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserResponseDto> getAllUsers() {
    return userRepository.findAll().stream()
        .map(UserMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void assignRole(Long id, UserRole newRole) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("User not found"));
    user.setUserRole(newRole);
    userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserResponseDto> getAllUsersPage(String role, Pageable pageable) {
    if (role != null && !role.isBlank()) {
      UserRole userRole = UserRole.valueOf(role.toUpperCase());
      return userRepository.findByUserRole(userRole, pageable).map(UserMapper::toDto);
    }
    return userRepository.findAll(pageable).map(UserMapper::toDto);
  }
}

