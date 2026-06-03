package com.kml.user.mapper;

import com.kml.user.dto.UserResponseDto;
import com.kml.user.entity.User;

public final class UserMapper {

  private UserMapper() {}

  public static UserResponseDto toDto(User entity) {
    if (entity == null) return null;

    return new UserResponseDto(
        entity.getId(),
        entity.getName(),
        entity.getUsername(),
        entity.getUserRole() != null ? entity.getUserRole().name() : null,
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
