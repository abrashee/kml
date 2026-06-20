package com.kml.services.inventory.dto;

import java.time.LocalDateTime;

public record InventoryItemResponseDto(
    Long id,
    Long ownerUserId,
    String sku,
    String name,
    int quantity,
    Long warehouseId,
    Long storageUnitId,
    int reorderThreshold,
    int safetyStockLevel,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long version) {
}
