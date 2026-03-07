package com.kml.capacity.mapper;

import com.kml.capacity.dto.InventoryItemResponseDto;
import com.kml.domain.inventory.InventoryItem;

/**
 * Mapper class for InventoryItem entity to InventoryItemResponseDto. Handles conversion and
 * includes audit fields from AuditableEntity.
 */
public final class InventoryMapper {

  private InventoryMapper() {
    // Private constructor to prevent instantiation
  }

  /**
   * Maps an InventoryItem entity to InventoryItemResponseDto.
   *
   * @param entity the InventoryItem entity
   * @return corresponding InventoryItemResponseDto or null if entity is null
   */
  public static InventoryItemResponseDto toDto(InventoryItem entity) {
    if (entity == null) return null;

    return new InventoryItemResponseDto(
        entity.getId(),
        entity.getSku(),
        entity.getName(),
        entity.getQuantity(),
        entity.getCreatedAt(), // from AuditableEntity
        entity.getUpdatedAt() // from AuditableEntity
        );
  }
}
