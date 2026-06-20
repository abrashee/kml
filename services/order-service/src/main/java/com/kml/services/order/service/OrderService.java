package com.kml.services.order.service;

import com.kml.services.order.dto.OrderRequestDto;
import com.kml.services.order.dto.OrderResponseDto;
import com.kml.services.order.entity.OrderStatus;
import java.util.List;

public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto request);

    OrderResponseDto getOrder(Long id);

    List<OrderResponseDto> getOrders(Long userId, OrderStatus status);

    OrderResponseDto updateStatus(Long id, OrderStatus status);

    OrderResponseDto assignWorker(Long id, Long workerId);
}
