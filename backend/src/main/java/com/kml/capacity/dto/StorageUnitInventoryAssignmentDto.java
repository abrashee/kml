package com.kml.capacity.dto;

import java.time.LocalDateTime;

public record StorageUnitInventoryAssignmentDto(
    Long storageUnitId,
    String storageUnitCode,
    Long inventoryItemId,
    String inventoryItemSku,
    String inventoryItemName,
    int assignedQuantity,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
