package com.kml.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kml.user.dto.UpdateStaffAccessDto;
import com.kml.user.dto.UserRequestDto;
import com.kml.user.dto.UserResponseDto;
import com.kml.user.entity.UserRole;

public interface UserService {

    // 1. Polymorphic creation (Handles Base, Manager, and Worker entities)
    UserResponseDto createUserWithDetails(UserRequestDto dto);

    // 2. Profile management for active sessions
    UserResponseDto updateMeProfile(String username, UserRequestDto dto);

    // 3. Paginated, role-filterable administration
    Page<UserResponseDto> getAllUsersPage(String role, Pageable pageable);

    // 4. Role modification utility
    void assignRole(Long id, UserRole newRole);

    // 5. Unique identifier lookup
    UserResponseDto getUserById(Long id);

    // 6. Security context mapping helper
    UserResponseDto getUserByUsername(String username);

    // 7. Validation check
    boolean existsByUsername(String username);

    UserResponseDto updateOperationalAccess(Long id, UpdateStaffAccessDto dto);

}
