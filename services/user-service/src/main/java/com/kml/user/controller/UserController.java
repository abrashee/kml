package com.kml.user.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kml.audit.userActivity.dto.UserActivityLogDto;
import com.kml.audit.userActivity.service.UserActivityLogService;
import com.kml.user.dto.UpdateStaffAccessDto;
import com.kml.user.dto.UserRequestDto;
import com.kml.user.dto.UserResponseDto;
import com.kml.user.dto.UserRoleAssignmentDto;
import com.kml.user.entity.UserRole;
import com.kml.user.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Users")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;
  private final UserActivityLogService userActivityLogService;

  public UserController(UserService userService, UserActivityLogService userActivityLogService) {
    this.userService = userService;
    this.userActivityLogService = userActivityLogService;
  }

  // ==========================================
  // CREATION ENDPOINTS
  // ==========================================

  /**
   * PUBLIC ENDPOINT: Used by the React frontend for Customer self-registration.
   * This is explicitly permitted in SecurityConfig.
   */
  @PostMapping("/register/customer")
  public ResponseEntity<UserResponseDto> registerCustomer(@RequestBody @Valid UserRequestDto requestDto) {
    // Force the role to CUSTOMER for security, ignoring any role sent in the payload
    requestDto.setUserRole(UserRole.CUSTOMER);
    UserResponseDto created = userService.createUserWithDetails(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  /**
   * INTERNAL ENDPOINT: Used by the Angular frontend for Admin operations.
   * Locked down to Admins only.
   */
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserRequestDto requestDto) {
    UserResponseDto created = userService.createUserWithDetails(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  // ==========================================
  // PROFILE & READ ENDPOINTS
  // ==========================================

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<Page<UserResponseDto>> getAllUsers(
      @RequestParam(required = false) String role,
      @PageableDefault(size = 20) Pageable pageable) {

    Page<UserResponseDto> users = userService.getAllUsersPage(role, pageable);
    return ResponseEntity.ok(users);
  }

  @PutMapping("/me/profile")
  public ResponseEntity<UserResponseDto> updateMe(Authentication auth,
                                                  @RequestBody UserRequestDto dto) {
      if (auth == null || !auth.isAuthenticated()) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
      return ResponseEntity.ok(userService.updateMeProfile(auth.getName(), dto));
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> getMe(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    UserResponseDto user = userService.getUserByUsername(authentication.getName());
    return ResponseEntity.ok(user);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
    UserResponseDto user = userService.getUserById(id);
    return ResponseEntity.ok(user);
  }

  // ==========================================
  // ADMINISTRATIVE & ACTIVITY ENDPOINTS
  // ==========================================

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}/role")
  public ResponseEntity<Void> assignRole(
      @PathVariable Long id, @RequestBody @Valid UserRoleAssignmentDto dto) {
    userService.assignRole(id, dto.getUserRole());
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{id}/activity")
  public ResponseEntity<List<UserActivityLogDto>> getUserActivity(@PathVariable Long id) {
    List<UserActivityLogDto> logs = userActivityLogService.getActivityLogsByUser(id);
    return ResponseEntity.ok(logs);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}/access")
  public ResponseEntity<UserResponseDto> updateStaffAccess(
          @PathVariable Long id,
          @RequestBody @Valid UpdateStaffAccessDto dto) {

      UserResponseDto updated = userService.updateOperationalAccess(id, dto);
      return ResponseEntity.ok(updated);
  }
}
