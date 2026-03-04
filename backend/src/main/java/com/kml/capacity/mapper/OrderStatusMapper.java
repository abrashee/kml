package com.kml.capacity.mapper;

import com.kml.capacity.dto.OrderStatusResponseDto;
import com.kml.domain.order.OrderStatus;

public final class OrderStatusMapper {

  private OrderStatusMapper() {}

  public static OrderStatusResponseDto toDto(OrderStatus entity) {
    return new OrderStatusResponseDto(
        entity.getId(),
        entity.getName(),
        entity.getDescription(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
