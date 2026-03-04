package com.kml.capacity.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kml.capacity.service.OrderStatusService;
import com.kml.domain.order.OrderStatus;
import com.kml.infra.OrderStatusRepository;

@Service
public class OrderStatusServiceImpl implements OrderStatusService {

  private final OrderStatusRepository orderStatusRepository;

  public OrderStatusServiceImpl(OrderStatusRepository orderStatusRepository) {
    this.orderStatusRepository = orderStatusRepository;
  }

  @Override
  @Transactional
  public OrderStatus createOrderStatus(String name, String description) {
    OrderStatus orderStatus = new OrderStatus(name, description);
    return orderStatusRepository.save(orderStatus);
  }

  @Override
  @Transactional
  public OrderStatus updateOrderStatus(Long id, String name, String description) {
    OrderStatus orderStatus =
        orderStatusRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order Status not found"));
    orderStatus.setName(name);
    orderStatus.setDescription(description);
    return orderStatusRepository.save(orderStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderStatus> getAllOrderStatuses() {
    return orderStatusRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<OrderStatus> getByName(String name) {
    return orderStatusRepository.findByName(name);
  }

  @Override
  @Transactional
  public void deleteOrderStatus(Long id) {
    if (orderStatusRepository.existsById(id)) {
      orderStatusRepository.deleteById(id);
    }
  }
}
