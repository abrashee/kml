package com.kml.inventory.mapper;

import com.kml.inventory.dto.InventoryItemResponseDto;
import com.kml.inventory.entity.InventoryItem;

public final class InventoryMapper {

  private InventoryMapper() {}

  public static InventoryItemResponseDto toDto(InventoryItem entity) {
    if (entity == null) return null;

    return new InventoryItemResponseDto(
        entity.getId(),
        entity.getSku(),
        entity.getName(),
        entity.getQuantity(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
