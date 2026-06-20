package com.kml.services.warehouse.dto;

import java.time.LocalDateTime;
import java.util.List;

public record WarehouseResponseDto(
    Long id,
    Long ownerUserId,
    String name,
    String address,
    List<StorageUnitResponseDto> storageUnits,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long version) {
}
