package com.kml.user.dto;

import java.time.LocalDateTime;

public record UserResponseDto(
    Long id,
    String name,
    String username,
    String userRole,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
