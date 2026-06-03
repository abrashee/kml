package com.kml.warehouse.storageUnit.dto;

import java.time.LocalDateTime;

public record StorageUnitResponseDto(
    Long id,
    String code,
    Long warehouseId,
    int capacity,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
