package com.kml.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private UserRole userRole;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  protected User() {}

  public User(String name, String username, String password, UserRole userRole) {
    validateName(name);
    validateUsername(username);
    validatePassword(password);
    validateUserRole(userRole);

    this.name = name;
    this.username = username;
    this.password = password;
    this.userRole = userRole;
  }

  private void validatePassword(String password) {
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Password is required");
    }
  }

  private void validateUserRole(UserRole userRole) {
    if (userRole == null) {
      throw new IllegalArgumentException("User role is required");
    }
  }

  public void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name is required");
    }
  }

  public void validateUsername(String username) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Username is required");
    }

    if (username.length() < 3) {
      throw new IllegalArgumentException("Username must be more than 3 characters");
    }
  }

  @PrePersist
  public void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public UserRole getUserRole() {
    return userRole;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
