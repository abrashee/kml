package com.kml.capacity.service;

import java.util.List;
import java.util.Optional;

import com.kml.domain.order.OrderStatus;

public interface OrderStatusService {

  OrderStatus createOrderStatus(String name, String description);

  OrderStatus updateOrderStatus(Long id, String name, String description);

  List<OrderStatus> getAllOrderStatuses();

  Optional<OrderStatus> getByName(String name);

  void deleteOrderStatus(Long id);
}
