package com.kml.audit.userActivity.dto;

import java.time.LocalDateTime;

public record UserActivityLogDto(
    Long id, Long userId, String activity, LocalDateTime createdAt, LocalDateTime updatedAt) {}
