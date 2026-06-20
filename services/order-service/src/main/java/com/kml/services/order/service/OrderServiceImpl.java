package com.kml.services.order.service;

import com.kml.services.common.events.OrderPlacedEvent;
import com.kml.services.order.dto.OrderRequestDto;
import com.kml.services.order.dto.OrderResponseDto;
import com.kml.services.order.entity.Order;
import com.kml.services.order.entity.OrderItem;
import com.kml.services.order.entity.OrderStatus;
import com.kml.services.order.mapper.OrderMapper;
import com.kml.services.order.repository.OrderRepository;
import com.kml.services.order.user.InternalUserProfileService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final InternalUserProfileService userProfileService;

    public OrderServiceImpl(
        OrderRepository orderRepository,
        OrderEventPublisher eventPublisher,
        InternalUserProfileService userProfileService) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.userProfileService = userProfileService;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {
        orderRepository.findByCode(request.code()).ifPresent(existing -> {
            throw new IllegalArgumentException("Order code already exists");
        });

        String shippingAddress = userProfileService.getShippingAddress(request.userId()).address();
        if (shippingAddress == null || shippingAddress.isBlank()) {
            throw new IllegalArgumentException("User must have a shipping address before placing an order");
        }

        Order order = new Order(request.code(), request.userId(), shippingAddress);
        request.items().forEach(item ->
            order.addItem(new OrderItem(item.sku(), item.quantity(), item.priceAtOrder())));

        Order saved = orderRepository.save(order);
        eventPublisher.publishOrderPlaced(new OrderPlacedEvent(
            saved.getId(),
            saved.getUserId(),
            shippingAddress,
            saved.getItems().stream()
                .map(item -> new OrderPlacedEvent.OrderLine(item.getSku(), item.getQuantity()))
                .toList(),
            Instant.now()));

        return OrderMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long id) {
        return orderRepository.findById(id)
            .map(OrderMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrders(Long userId, OrderStatus status) {
        List<Order> orders;
        if (userId != null) {
            orders = orderRepository.findByUserId(userId);
        } else if (status != null) {
            orders = orderRepository.findByStatus(status);
        } else {
            orders = orderRepository.findAll();
        }
        return orders.stream().map(OrderMapper::toDto).toList();
    }

    @Override
    @Transactional
    public OrderResponseDto updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(status);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponseDto assignWorker(Long id, Long workerId) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.assignWorker(workerId);
        return OrderMapper.toDto(orderRepository.save(order));
    }
}
