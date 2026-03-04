package com.kml.capacity.service;

import java.util.List;

import com.kml.capacity.dto.OrderItemRequestDto;
import com.kml.capacity.dto.OrderResponseDto;
import com.kml.domain.user.User;

public interface OrderService {

  OrderResponseDto createOrder(
      String code, Long statusId, List<OrderItemRequestDto> items, User user);

  OrderResponseDto updateOrder(Long id, Long statusId, List<OrderItemRequestDto> items, User user);

  List<OrderResponseDto> getAllOrders();

  OrderResponseDto getOrderById(Long id);

  void deleteOrder(Long id);
}
