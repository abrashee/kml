package com.kml.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kml.user.entity.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRequestDto {
  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Username is required")
  @Size(min = 3)
  private String username;

  @NotBlank(message = "Password is required")
  @Size(min = 3)
  private String password;

  @NotNull(message = "User role is required")
  @JsonProperty("role") // <-- ADD THIS: Jackson will read incoming "role" into this variable
  private UserRole userRole;

  // Added to support user profile updates and creation defaults
  private String avatarUrl;

  private String address; // <--- Make sure this exists!

  // Logistics parameters for specialized staff mapping
  private Long warehouseId;
  private Long managerId;

  // Existing Getters and Setters
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public UserRole getUserRole() { return userRole; }
  public void setUserRole(UserRole userRole) { this.userRole = userRole; }

  // New Getters and Setters that resolve the controller error
  public String getAvatarUrl() { return avatarUrl; }
  public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

  public Long getWarehouseId() { return warehouseId; }
  public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

  public Long getManagerId() { return managerId; }
  public void setManagerId(Long managerId) { this.managerId = managerId; }

  public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
