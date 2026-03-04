package com.kml.capacity.mapper;

import com.kml.capacity.dto.InventoryItemResponseDto;
import com.kml.domain.inventory.InventoryItem;

public class InventoryMapper {
  public static InventoryItemResponseDto toDto(InventoryItem entity) {
    return new InventoryItemResponseDto(
        entity.getId(),
        entity.getSku(),
        entity.getName(),
        entity.getQuantity(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
