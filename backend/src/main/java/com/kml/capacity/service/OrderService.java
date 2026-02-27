package com.kml.capacity.service;

import java.util.List;

import com.kml.capacity.dto.OrderItemRequestDto;
import com.kml.capacity.dto.OrderResponseDto;

public interface OrderService {

  OrderResponseDto createOrder(String code, Long statudId, List<OrderItemRequestDto> items);

  OrderResponseDto updateOrder(Long id, Long statudId, List<OrderItemRequestDto> items);

  List<OrderResponseDto> getAllOrders();

  OrderResponseDto getOrderById(Long id);

  void deleteOrder(Long id);
}
