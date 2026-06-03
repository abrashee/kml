package com.kml.order.service;

import java.util.List;

import com.kml.order.dto.OrderStatusResponseDto;
import com.kml.user.entity.User;

public interface OrderStatusService {

  OrderStatusResponseDto createOrderStatus(User owner, String name, String description);

  OrderStatusResponseDto updateOrderStatus(Long id, String name, String description);

  List<OrderStatusResponseDto> getAllOrderStatuses();

  OrderStatusResponseDto getByName(String name);

  void deleteOrderStatus(Long id);
}
