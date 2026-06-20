package com.kml.services.inventory.mapper;

import com.kml.services.inventory.dto.InventoryItemResponseDto;
import com.kml.services.inventory.entity.InventoryItem;

public final class InventoryMapper {

    private InventoryMapper() {
    }

    public static InventoryItemResponseDto toDto(InventoryItem item) {
        return new InventoryItemResponseDto(
            item.getId(),
            item.getOwnerUserId(),
            item.getSku(),
            item.getName(),
            item.getQuantity(),
            item.getWarehouseId(),
            item.getStorageUnitId(),
            item.getReorderThreshold(),
            item.getSafetyStockLevel(),
            item.getCreatedAt(),
            item.getUpdatedAt(),
            item.getVersion());
    }
}
