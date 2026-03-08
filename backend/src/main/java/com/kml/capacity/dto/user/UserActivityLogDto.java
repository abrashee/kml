package com.kml.capacity.dto.user;

import java.time.LocalDateTime;

public record UserActivityLogDto(
    Long id, Long userId, String activity, LocalDateTime createdAt, LocalDateTime updatedAt) {}
