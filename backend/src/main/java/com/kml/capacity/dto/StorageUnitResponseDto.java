package com.kml.capacity.dto;

import java.time.LocalDateTime;

public record StorageUnitResponseDto(
    Long id,
    String code,
    Long warehouseId,
    int capacity,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
