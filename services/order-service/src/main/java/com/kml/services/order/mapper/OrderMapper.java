package com.kml.services.order.mapper;

import com.kml.services.order.dto.OrderItemResponseDto;
import com.kml.services.order.dto.OrderResponseDto;
import com.kml.services.order.entity.Order;
import com.kml.services.order.entity.OrderItem;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponseDto toDto(Order order) {
        return new OrderResponseDto(
            order.getId(),
            order.getCode(),
            order.getUserId(),
            order.getShippingAddress(),
            order.getStatus(),
            order.getAssignedWorkerId(),
            order.getItems().stream().map(OrderMapper::toDto).toList(),
            order.getCreatedAt(),
            order.getUpdatedAt(),
            order.getVersion());
    }

    private static OrderItemResponseDto toDto(OrderItem item) {
        return new OrderItemResponseDto(
            item.getId(),
            item.getSku(),
            item.getQuantity(),
            item.getPriceAtOrder(),
            item.getWarehouseId());
    }
}
