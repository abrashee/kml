package com.kml.capacity.mapper;

import java.time.LocalDateTime;

import com.kml.capacity.dto.order.OrderStatusResponseDto;
import com.kml.domain.order.OrderStatus;

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
