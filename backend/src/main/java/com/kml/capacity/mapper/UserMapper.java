package com.kml.capacity.mapper;

import com.kml.capacity.dto.UserResponseDto;
import com.kml.domain.user.User;

public final class UserMapper {

  private UserMapper() {}

  public static UserResponseDto toDto(User entity) {
    UserResponseDto dto = new UserResponseDto();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setUsername(entity.getUsername());
    dto.setUserRole(entity.getUserRole().name());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setUpdatedAt(entity.getUpdatedAt());
    return dto;
  }
}
