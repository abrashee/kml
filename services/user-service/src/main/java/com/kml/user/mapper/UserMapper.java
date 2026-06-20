package com.kml.user.mapper;

import com.kml.user.dto.UserResponseDto;
import com.kml.user.entity.User;
import com.kml.user.entity.Manager;
import com.kml.user.entity.Worker;

public final class UserMapper {

    private UserMapper() {}

    public static UserResponseDto toDto(User entity) {
        if (entity == null) return null;

        Long warehouseId = null;
        Long managerId = null;

        if (entity instanceof Manager manager) {
            warehouseId = manager.getWarehouseId();
        } else if (entity instanceof Worker worker) {
            warehouseId = worker.getWarehouseId();
            managerId = worker.getManagerId();
        }

        return new UserResponseDto(
            entity.getId(),
            entity.getName(),
            entity.getUsername(),
            entity.getUserRole() != null ? entity.getUserRole().name() : null,
            entity.getAddress(),
            entity.getAvatarUrl(),
            warehouseId,
            managerId,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}