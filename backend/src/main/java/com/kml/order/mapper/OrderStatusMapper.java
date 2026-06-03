package com.kml.order.mapper;

import java.time.LocalDateTime;

import com.kml.order.dto.OrderStatusResponseDto;
import com.kml.order.entity.OrderStatus;

public final class OrderStatusMapper {

  private OrderStatusMapper() {}

  public static OrderStatusResponseDto toDto(OrderStatus entity) {
    if (entity == null) return null;

    LocalDateTime createdAt = entity.getCreatedAt();
    LocalDateTime updatedAt = entity.getUpdatedAt();

    return new OrderStatusResponseDto(
        entity.getId(), entity.getName(), entity.getDescription(), createdAt, updatedAt);
  }
}
