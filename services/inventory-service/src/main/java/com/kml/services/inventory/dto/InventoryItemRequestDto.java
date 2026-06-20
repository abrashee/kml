package com.kml.services.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InventoryItemRequestDto(
    @NotNull Long ownerUserId,
    @NotBlank String sku,
    @NotBlank String name,
    @Min(0) int quantity,
    @NotNull Long warehouseId,
    @NotNull Long storageUnitId,
    @Min(0) int reorderThreshold,
    @Min(0) int safetyStockLevel) {
}
