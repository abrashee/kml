package com.kml.capacity.dto;

import java.time.LocalDateTime;

/** Immutable DTO for user activity logs. Use constructor to set createdAt and updatedAt. */
public record UserActivityLogDto(
    Long id, Long userId, String activity, LocalDateTime createdAt, LocalDateTime updatedAt) {}
