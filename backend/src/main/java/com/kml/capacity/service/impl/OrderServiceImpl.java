package com.kml.capacity.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kml.capacity.dto.OrderItemRequestDto;
import com.kml.capacity.dto.OrderResponseDto;
import com.kml.capacity.mapper.OrderMapper;
import com.kml.capacity.security.CurrentUserProvider;
import com.kml.capacity.service.OrderService;
import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.order.Order;
import com.kml.domain.order.OrderItem;
import com.kml.domain.order.OrderStatus;
import com.kml.domain.user.User;
import com.kml.infra.InventoryRepository;
import com.kml.infra.OrderRepository;
import com.kml.infra.OrderStatusRepository;

@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final OrderStatusRepository orderStatusRepository;
  private final InventoryRepository inventoryRepository;
  private final CurrentUserProvider currentUserProvider;

  public OrderServiceImpl(
      OrderRepository orderRepository,
      OrderStatusRepository orderStatusRepository,
      InventoryRepository inventoryRepository,
      CurrentUserProvider currentUserProvider) {

    this.orderRepository = orderRepository;
    this.orderStatusRepository = orderStatusRepository;
    this.inventoryRepository = inventoryRepository;
    this.currentUserProvider = currentUserProvider;
  }

  @Override
  @Transactional
  public OrderResponseDto createOrder(
      String code, Long statusId, List<OrderItemRequestDto> items, User user) {

    if (user == null) {
      user = currentUserProvider.getCurrentUser();
    }

    OrderStatus status =
        orderStatusRepository
            .findById(statusId)
            .orElseThrow(() -> new IllegalArgumentException("Order status not found"));

    Order order = new Order(code, status, user);

    mapToOrderItems(items).forEach(order::addItem);

    Order saved = orderRepository.save(order);
    return OrderMapper.toDto(saved);
  }

  @Override
  @Transactional
  public OrderResponseDto updateOrder(
      Long id, Long statusId, List<OrderItemRequestDto> items, User user) {

    if (user == null) {
      user = currentUserProvider.getCurrentUser();
    }

    Order order =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    if (!order.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("User cannot update someone else's order");
    }

    OrderStatus status =
        orderStatusRepository
            .findById(statusId)
            .orElseThrow(() -> new IllegalArgumentException("Order status not found"));

    order.setStatus(status);

    order.replaceItems(mapToOrderItems(items));

    Order saved = orderRepository.save(order);
    return OrderMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderResponseDto> getAllOrders() {

    return orderRepository.findAll().stream().map(OrderMapper::toDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public OrderResponseDto getOrderById(Long id) {

    Order order =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    return OrderMapper.toDto(order);
  }

  @Override
  @Transactional
  public void deleteOrder(Long id) {

    Order order =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    orderRepository.delete(order);
  }

  private List<OrderItem> mapToOrderItems(List<OrderItemRequestDto> items) {

    if (items == null || items.isEmpty()) {
      throw new IllegalArgumentException("Order must contain at least one item");
    }

    List<OrderItem> mapped = new ArrayList<>();

    for (OrderItemRequestDto dto : items) {

      InventoryItem inventoryItem =
          inventoryRepository
              .findById(dto.getInventoryItemId())
              .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));

      if (dto.getQuantity() <= 0) {
        throw new IllegalArgumentException("Quantity must be positive");
      }

      if (dto.getPriceAtOrder() == null) {
        throw new IllegalArgumentException("Price must be provided");
      }

      mapped.add(new OrderItem(inventoryItem, dto.getQuantity(), dto.getPriceAtOrder()));
    }

    return mapped;
  }
}
