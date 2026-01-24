package com.kml.capacity.service;

import com.kml.capacity.dto.OrderItemRequestDto;
import com.kml.domain.order.Order;
import java.util.List;

public interface OrderService {

  Order createOrder(String code, Long statudId, List<OrderItemRequestDto> items);

  Order updateOrder(Long id, Long statudId, List<OrderItemRequestDto> items);

  List<Order> getAllOrders();

  Order getOrderById(Long id);

  void deleteOrder(Long id);
}
