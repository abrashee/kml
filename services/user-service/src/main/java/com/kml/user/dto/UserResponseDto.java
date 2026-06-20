package com.kml.user.dto;

import java.time.LocalDateTime;

public record UserResponseDto(
    Long id,
    String name,
    String username,
    String userRole,
    String address,
    String avatarUrl,
    Long warehouseId,
    Long managerId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}