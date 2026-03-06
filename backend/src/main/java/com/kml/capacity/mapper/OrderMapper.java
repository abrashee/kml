package com.kml.capacity.mapper;

import java.util.List;

import com.kml.capacity.dto.OrderItemResponseDto;
import com.kml.capacity.dto.OrderResponseDto;
import com.kml.domain.order.Order;
import com.kml.domain.order.OrderItem;

public final class OrderMapper {

  private OrderMapper() {}

  public static OrderResponseDto toDto(Order entity) {

    if (entity == null) return null;

    List<OrderItemResponseDto> items = entity.getItems().stream().map(OrderMapper::toDto).toList();

    return new OrderResponseDto(
        entity.getId(),
        entity.getCode(),
        entity.getStatus() != null ? entity.getStatus().getId() : null,
        entity.getStatus() != null ? entity.getStatus().getName() : null,
        entity.getUser() != null ? entity.getUser().getId() : null,
        entity.getUser() != null ? entity.getUser().getUsername() : null,
        items,
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  private static OrderItemResponseDto toDto(OrderItem item) {

    Long inventoryItemId = item.getInventoryItem() != null ? item.getInventoryItem().getId() : null;

    return new OrderItemResponseDto(
        item.getId(), inventoryItemId, item.getQuantity(), item.getPriceAtOrder());
  }
}
