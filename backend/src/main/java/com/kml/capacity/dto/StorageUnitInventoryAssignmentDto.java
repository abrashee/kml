package com.kml.capacity.dto;

public record StorageUnitInventoryAssignmentDto(
    Long storageUnitId,
    String storageUnitCode,
    Long inventoryItemId,
    String inventoryItemSku,
    String inventoryItemName,
    int assignedQuantity) {}
