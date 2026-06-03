package com.kml.user.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kml.user.dto.UserResponseDto;
import com.kml.user.entity.UserRole;

public interface UserService {

  UserResponseDto createUser(String name, String username, String password, UserRole userRole);

  boolean existsByUsername(String username);

  UserResponseDto getUserById(Long id);

  List<UserResponseDto> getAllUsers();

  void assignRole(Long id, UserRole newRole);

  Page<UserResponseDto> getAllUsersPage(String role, Pageable pageable);
}
