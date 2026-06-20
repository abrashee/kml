package com.kml.services.warehouse.dto;

public record StorageUnitResponseDto(
    Long id,
    Long warehouseId,
    String code,
    int capacity,
    int remainingCapacity) {
}
