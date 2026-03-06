package com.kml.capacity.mapper;

import java.util.List;

import com.kml.capacity.dto.OrderItemResponseDto;
import com.kml.capacity.dto.OrderResponseDto;
import com.kml.domain.order.Order;
import com.kml.domain.order.OrderItem;

/**
 * Mapper class for converting Order entities to DTOs. Handles nested OrderItem mapping and includes
 * audit fields.
 */
public final class OrderMapper {

  private OrderMapper() {
    // Private constructor to prevent instantiation
  }

  /**
   * Maps an Order entity to OrderResponseDto.
   *
   * @param entity the Order entity
   * @return corresponding OrderResponseDto, or null if entity is null
   */
  public static OrderResponseDto toDto(Order entity) {
    if (entity == null) return null;

    List<OrderItemResponseDto> items = entity.getItems().stream().map(OrderMapper::toDto).toList();

    return new OrderResponseDto(
        entity.getId(),
        entity.getCode(),
        entity.getStatus() != null ? entity.getStatus().getId() : null,
        entity.getStatus() != null ? entity.getStatus().getName() : null,
        entity.getOwner() != null ? entity.getOwner().getId() : null,
        entity.getOwner() != null ? entity.getOwner().getUsername() : null,
        items,
        entity.getCreatedAt(), // from AuditableEntity
        entity.getUpdatedAt() // from AuditableEntity
        );
  }

  /**
   * Maps a single OrderItem entity to OrderItemResponseDto.
   *
   * @param item the OrderItem entity
   * @return corresponding OrderItemResponseDto
   */
  private static OrderItemResponseDto toDto(OrderItem item) {
    if (item == null) return null;

    Long inventoryItemId = item.getInventoryItem() != null ? item.getInventoryItem().getId() : null;

    return new OrderItemResponseDto(
        item.getId(), inventoryItemId, item.getQuantity(), item.getPriceAtOrder());
  }
}
