package com.kml.services.order.service;

import com.kml.services.common.security.jwt.JwtAuthenticatedUser;
import com.kml.services.order.dto.OrderItemsUpdateRequestDto;
import com.kml.services.order.dto.OrderRequestDto;
import com.kml.services.order.dto.OrderResponseDto;
import com.kml.services.order.entity.OrderStatus;
import java.util.List;

public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto request, JwtAuthenticatedUser principal);

    OrderResponseDto getOrder(Long id, JwtAuthenticatedUser principal);

    List<OrderResponseDto> getOrders(Long userId, OrderStatus status, JwtAuthenticatedUser principal);

    OrderResponseDto updateItems(Long id, OrderItemsUpdateRequestDto request, JwtAuthenticatedUser principal);

    OrderResponseDto updateStatus(Long id, OrderStatus status, JwtAuthenticatedUser principal);

    OrderResponseDto assignWorker(Long id, Long workerId, JwtAuthenticatedUser principal);
}
