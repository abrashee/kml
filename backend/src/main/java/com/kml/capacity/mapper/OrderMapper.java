package com.kml.capacity.mapper;

import java.util.ArrayList;
import java.util.List;

import com.kml.capacity.dto.OrderItemResponseDto;
import com.kml.capacity.dto.OrderResponseDto;
import com.kml.domain.order.Order;
import com.kml.domain.order.OrderItem;

public final class OrderMapper {

  private OrderMapper() {}

  public static OrderResponseDto toDto(Order entity) {
    if (entity == null) {
      return null;
    }

    OrderResponseDto dto = new OrderResponseDto();
    dto.setId(entity.getId());
    dto.setCode(entity.getCode());

    dto.setStatusId(entity.getStatus() != null ? entity.getStatus().getId() : null);
    dto.setStatusName(entity.getStatus() != null ? entity.getStatus().getName() : null);

    dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
    dto.setUsername(entity.getUser() != null ? entity.getUser().getUsername() : null);

    dto.setCreatedAt(entity.getCreatedAt());
    dto.setUpdatedAt(entity.getUpdatedAt());

    List<OrderItemResponseDto> items = new ArrayList<>();
    for (OrderItem item : entity.getItems()) {
      items.add(toDto(item));
    }
    dto.setItems(items);

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
