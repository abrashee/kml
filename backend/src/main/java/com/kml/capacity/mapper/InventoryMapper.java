package com.kml.capacity.mapper;

import com.kml.capacity.dto.InventoryItemResponseDto;
import com.kml.domain.inventory.InventoryItem;

public class InventoryMapper {
  public static InventoryItemResponseDto toDto(InventoryItem entity) {
    InventoryItemResponseDto dto = new InventoryItemResponseDto();
    dto.setId(entity.getId());
    dto.setSku(entity.getSku());
    dto.setName(entity.getName());
    dto.setQuantity(entity.getQuantity());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setUpdatedAt(entity.getUpdatedAt());
    return dto;
  }
}
