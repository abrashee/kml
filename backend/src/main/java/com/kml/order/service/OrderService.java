package com.kml.order.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kml.order.dto.OrderItemRequestDto;
import com.kml.order.dto.OrderResponseDto;
import com.kml.user.entity.User;

public interface OrderService {

  OrderResponseDto createOrder(
      String code, Long statusId, List<OrderItemRequestDto> items, User user);

  OrderResponseDto updateOrder(Long id, Long statusId, List<OrderItemRequestDto> items, User user);

  List<OrderResponseDto> getAllOrders();

  Page<OrderResponseDto> getAllOrdersPage(Pageable pageable);

  OrderResponseDto getOrderById(Long id);

  void deleteOrder(Long id);
}
