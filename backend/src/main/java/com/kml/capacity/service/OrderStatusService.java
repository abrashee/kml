package com.kml.capacity.service;

import java.util.List;

import com.kml.capacity.dto.order.OrderStatusResponseDto;
import com.kml.domain.user.User;

public interface OrderStatusService {

  OrderStatusResponseDto createOrderStatus(User owner, String name, String description);

  OrderStatusResponseDto updateOrderStatus(Long id, String name, String description);

  List<OrderStatusResponseDto> getAllOrderStatuses();

  OrderStatusResponseDto getByName(String name);

  void deleteOrderStatus(Long id);
}
