package com.kml.capacity.service.serviceImplementation;

import com.kml.capacity.dto.OrderItemRequestDto;
import com.kml.capacity.service.OrderService;
import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.order.Order;
import com.kml.domain.order.OrderItem;
import com.kml.domain.order.OrderStatus;
import com.kml.infra.InventoryRepository;
import com.kml.infra.OrderRepository;
import com.kml.infra.OrderStatusRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImplementation implements OrderService {

  private final OrderRepository orderRepository;
  private final OrderStatusRepository orderStatusRepository;
  private final InventoryRepository inventoryRepository;

  ////////////// ******************************* StATIC Value only for
  // Testing******************************* */
  private BigDecimal priceAtOrder = new BigDecimal("100.0");
  ;

  public OrderServiceImplementation(
      OrderRepository orderRepository,
      OrderStatusRepository orderStatusRepository,
      InventoryRepository inventoryRepository) {
    this.orderRepository = orderRepository;
    this.orderStatusRepository = orderStatusRepository;
    this.inventoryRepository = inventoryRepository;
  }

  @Override
  @Transactional
  public Order createOrder(String code, Long statusId, List<OrderItemRequestDto> items) {

    OrderStatus status =
        orderStatusRepository
            .findById(statusId)
            .orElseThrow(() -> new IllegalArgumentException("Order status not found"));

    Order order = new Order(code, status);

    for (OrderItemRequestDto dto : items) {
      InventoryItem inventoryItem =
          inventoryRepository
              .findById(dto.getInventoryItemId())
              .orElseThrow(() -> new IllegalArgumentException("Invenotry not found"));
      OrderItem orderItem = new OrderItem(inventoryItem, dto.getQuantity(), dto.getPriceAtOrder());

      order.addItem(orderItem);
    }

    return this.orderRepository.save(order);
  }

  @Override
  @Transactional
  public Order updateOrder(Long id, Long statusId, List<OrderItemRequestDto> items) {

    Order order =
        this.orderRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    OrderStatus status =
        orderStatusRepository
            .findById(statusId)
            .orElseThrow(() -> new IllegalArgumentException("Order status not found"));

    order.setStatus(status);

    order.getItems().clear();

    for (OrderItemRequestDto dto : items) {
      InventoryItem inventoryItem =
          inventoryRepository
              .findById(dto.getInventoryItemId())
              .orElseThrow(() -> new IllegalArgumentException("Inventory Item not found"));

      OrderItem orderItem = new OrderItem(inventoryItem, dto.getQuantity(), dto.getPriceAtOrder());
      order.addItem(orderItem);
    }

    return this.orderRepository.save(order);
  }

  @Override
  public List<Order> getAllOrders() {
    return this.orderRepository.findAll();
  }

  @Override
  public Order getOrderById(Long id) {
    return this.orderRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));
  }

  @Override
  public void deleteOrder(Long id) {
    if (this.orderRepository.existsById(id)) {
      this.orderRepository.deleteById(id);
    }
  }

  private OrderItem mapToOrderItem(OrderItemRequestDto dto) {

    InventoryItem inventoryItem =
        inventoryRepository
            .findById(dto.getInventoryItemId())
            .orElseThrow(() -> new IllegalArgumentException("Inventory Item not found"));
    return new OrderItem(inventoryItem, dto.getQuantity(), dto.getPriceAtOrder());
  }
}
