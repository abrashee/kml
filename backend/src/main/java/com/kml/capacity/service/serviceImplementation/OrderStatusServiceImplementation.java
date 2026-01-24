package com.kml.capacity.service.serviceImplementation;

import com.kml.capacity.service.OrderStatusService;
import com.kml.domain.order.OrderStatus;
import com.kml.infra.OrderStatusRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusServiceImplementation implements OrderStatusService {
  private final OrderStatusRepository orderStatusRepository;

  public OrderStatusServiceImplementation(OrderStatusRepository orderStatusRepository) {
    this.orderStatusRepository = orderStatusRepository;
  }

  @Override
  @Transactional
  public OrderStatus createOrderStatus(String name, String description) {
    OrderStatus orderStatus = new OrderStatus(name, description);
    return this.orderStatusRepository.save(orderStatus);
  }

  @Override
  @Transactional
  public OrderStatus updateOrderStatus(Long id, String name, String description) {
    OrderStatus orderStatus =
        this.orderStatusRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order Status not found"));
    orderStatus.setName(name);
    orderStatus.setDescription(description);
    return this.orderStatusRepository.save(orderStatus);
  }

  @Override
  public List<OrderStatus> getAllOrderStatuses() {
    return this.orderStatusRepository.findAll();
  }

  @Override
  public Optional<OrderStatus> getByName(String name) {
    return this.orderStatusRepository.findByName(name);
  }

  @Override
  @Transactional
  public void deleteOrderStatus(Long id) {
    if (this.orderStatusRepository.existsById(id)) {
      this.orderStatusRepository.deleteById(id);
    }
  }
}
