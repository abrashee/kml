package com.kml.services.order.dto;

import com.kml.services.order.entity.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
    Long id,
    String code,
    Long userId,
    String shippingAddress,
    OrderStatus status,
    Long assignedWorkerId,
    List<OrderItemResponseDto> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long version) {
}
