package com.kml.user.dto;

import com.kml.user.entity.UserRole;

import jakarta.validation.constraints.NotNull;

public class UserRoleAssignmentDto {

  @NotNull
  private UserRole userRole;

  public UserRole getUserRole() {
    return userRole;
  }

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }
}
