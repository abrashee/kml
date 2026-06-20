package com.kml.audit.userActivity.mapper;

import com.kml.audit.userActivity.dto.UserActivityLogDto;
import com.kml.audit.userActivity.entity.UserActivityLog;

public final class UserActivityLogMapper {

  private UserActivityLogMapper() {}

  public static UserActivityLogDto toDto(UserActivityLog entity) {
    if (entity == null) return null;

    return new UserActivityLogDto(
        entity.getId(),
        entity.getUser() != null ? entity.getUser().getId() : null,
        entity.getAction(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
