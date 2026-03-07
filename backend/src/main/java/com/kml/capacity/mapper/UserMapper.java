package com.kml.capacity.mapper;

import com.kml.capacity.dto.UserResponseDto;
import com.kml.domain.user.User;

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
