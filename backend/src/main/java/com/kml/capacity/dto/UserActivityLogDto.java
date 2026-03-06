package com.kml.capacity.dto;

import java.time.LocalDateTime;

public record UserActivityLogDto(
    String username, String action, String entity, Long entityId, LocalDateTime createdAt) {}
