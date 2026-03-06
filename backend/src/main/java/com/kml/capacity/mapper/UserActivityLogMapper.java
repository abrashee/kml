package com.kml.capacity.mapper;

import com.kml.capacity.dto.UserActivityLogDto;
import com.kml.domain.audit.UserActivityLog;

public final class UserActivityLogMapper {

  private UserActivityLogMapper() {}

  public static UserActivityLogDto toDto(UserActivityLog entity) {
    if (entity == null) return null;

    return new UserActivityLogDto(
        entity.getUser().getUsername(),
        entity.getAction(),
        entity.getEntity(),
        entity.getEntityId(),
        entity.getCreatedAt());
  }
}
