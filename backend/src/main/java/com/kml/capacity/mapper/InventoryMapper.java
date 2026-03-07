package com.kml.capacity.mapper;

import com.kml.capacity.dto.InventoryItemResponseDto;
import com.kml.domain.inventory.InventoryItem;

public final class InventoryMapper {

  private InventoryMapper() {
  }

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
