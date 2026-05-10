package com.kml.capacity.dto.user;

import com.kml.domain.user.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRequestDto {
  @NotBlank private String name;

  @NotBlank
  @Size(min = 3)
  private String username;

  @NotBlank
  @Size(min = 3)
  private String password;

  @NotNull(message = "User role is required")
  private UserRole userRole;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserRole getUserRole() {
    return userRole;
  }

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }
}
