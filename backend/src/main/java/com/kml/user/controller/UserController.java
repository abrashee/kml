package com.kml.user.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kml.audit.userActivity.dto.UserActivityLogDto;
import com.kml.audit.userActivity.service.UserActivityLogService;
import com.kml.user.dto.UserRequestDto;
import com.kml.user.dto.UserResponseDto;
import com.kml.user.dto.UserRoleAssignmentDto;
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

  @PostMapping
  public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserRequestDto requestDto) {

    UserResponseDto created =
        userService.createUser(
            requestDto.getName(),
            requestDto.getUsername(),
            requestDto.getPassword(),
            requestDto.getUserRole());

    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<Page<UserResponseDto>> getAllUsers(
      @RequestParam(required = false) String role,
      @PageableDefault(size = 20) Pageable pageable) {

    Page<UserResponseDto> users = userService.getAllUsersPage(role, pageable);
    return ResponseEntity.ok(users);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
    UserResponseDto user = userService.getUserById(id);
    return ResponseEntity.ok(user);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}/role")
  public ResponseEntity<Void> assignRole(
      @PathVariable Long id, @RequestBody @Valid UserRoleAssignmentDto dto) {
    userService.assignRole(id, dto.getUserRole());
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/{id}/activity")
  public ResponseEntity<List<UserActivityLogDto>> getUserActivity(@PathVariable Long id) {
    List<UserActivityLogDto> logs = userActivityLogService.getActivityLogsByUser(id);
    return ResponseEntity.ok(logs);
  }
}

