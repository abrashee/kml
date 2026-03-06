package com.kml.capacity.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kml.capacity.dto.OrderStatusResponseDto;
import com.kml.capacity.mapper.OrderStatusMapper;
import com.kml.capacity.service.OrderStatusService;
import com.kml.domain.order.OrderStatus;
import com.kml.domain.user.User;
import com.kml.infra.OrderStatusRepository;

@Service
public class OrderStatusServiceImpl implements OrderStatusService {

  private final OrderStatusRepository repository;

  public OrderStatusServiceImpl(OrderStatusRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional
  public OrderStatusResponseDto createOrderStatus(User owner, String name, String description) {
    OrderStatus status = OrderStatus.create(owner, name, description);
    return OrderStatusMapper.toDto(repository.save(status));
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderStatusResponseDto> getAllOrderStatuses() {
    return repository.findAll().stream().map(OrderStatusMapper::toDto).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public OrderStatusResponseDto getByName(String name) {
    return repository.findByName(name).map(OrderStatusMapper::toDto).orElse(null);
  }

  @Override
  @Transactional
  public OrderStatusResponseDto updateOrderStatus(Long id, String name, String description) {
    OrderStatus existing =
        repository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("OrderStatus not found"));

    existing.update(name, description);

    return OrderStatusMapper.toDto(repository.save(existing));
  }

  @Override
  @Transactional
  public void deleteOrderStatus(Long id) {
    OrderStatus existing =
        repository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("OrderStatus not found"));

    repository.delete(existing);
  }
}
