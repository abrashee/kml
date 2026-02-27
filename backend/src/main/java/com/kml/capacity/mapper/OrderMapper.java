package com.kml.capacity.mapper;

import com.kml.capacity.dto.OrderItemResponseDto;
import com.kml.capacity.dto.OrderResponseDto;
import com.kml.domain.order.Order;
import com.kml.domain.order.OrderItem;

public class OrderMapper {
  public static OrderResponseDto toDto(Order entity) {
    OrderResponseDto dto = new OrderResponseDto();
    dto.setId(entity.getId());
    dto.setCode(entity.getCode());
    dto.setStatusName(entity.getStatus().getName());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setUpdatedAt(entity.getUpdatedAt());
    dto.setItems(entity.getItems().stream().map(OrderMapper::toDto).toList());

    return dto;
  }

  private static OrderItemResponseDto toDto(OrderItem item) {
    OrderItemResponseDto dto = new OrderItemResponseDto();
    dto.setId(item.getId());
    dto.setInventoryItemId(item.getInventoryItem().getId());
    dto.setQuantity(item.getQuantity());
    dto.setPriceAtOrder(item.getPriceAtOrder());
    return dto;
  }
}
