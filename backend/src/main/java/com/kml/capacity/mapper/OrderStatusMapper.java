package com.kml.capacity.mapper;

import com.kml.capacity.dto.OrderStatusResponseDto;
import com.kml.domain.order.OrderStatus;

public final class OrderStatusMapper {

  private OrderStatusMapper() {}

  public static OrderStatusResponseDto toDto(OrderStatus entity) {
    OrderStatusResponseDto dto = new OrderStatusResponseDto();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setDescription(entity.getDescription());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setUpdatedAt(entity.getUpdatedAt());
    return dto;
  }
}
