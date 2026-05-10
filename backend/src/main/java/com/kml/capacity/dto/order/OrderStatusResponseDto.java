package com.kml.capacity.dto.order;

import java.time.LocalDateTime;

public record OrderStatusResponseDto(
    Long id, String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {}
