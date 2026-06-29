package com.kml.services.order.service;

import com.kml.services.common.events.OrderPlacedEvent;
import com.kml.services.common.security.jwt.JwtAuthenticatedUser;
import com.kml.services.order.dto.OrderItemsUpdateRequestDto;
import com.kml.services.order.dto.OrderRequestDto;
import com.kml.services.order.dto.OrderResponseDto;
import com.kml.services.order.entity.Order;
import com.kml.services.order.entity.OrderItem;
import com.kml.services.order.entity.OrderStatus;
import com.kml.services.order.mapper.OrderMapper;
import com.kml.services.order.repository.OrderRepository;
import com.kml.services.order.user.InternalUserProfileService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final InternalUserProfileService userProfileService;
    private final Counter ordersCreatedCounter;

    public OrderServiceImpl(
        OrderRepository orderRepository,
        OrderEventPublisher eventPublisher,
        InternalUserProfileService userProfileService,
        MeterRegistry meterRegistry) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.userProfileService = userProfileService;
        this.ordersCreatedCounter = Counter.builder("kml_orders_created")
            .description("Total number of successfully created orders")
            .register(meterRegistry);
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request, JwtAuthenticatedUser principal) {
        orderRepository.findByCode(request.code()).ifPresent(existing -> {
            throw new IllegalArgumentException("Order code already exists");
        });

        Long effectiveUserId = customerUserIdOrRequested(request.userId(), principal);
        String shippingAddress = userProfileService.getShippingAddress(effectiveUserId).address();
        if (shippingAddress == null || shippingAddress.isBlank()) {
            throw new IllegalArgumentException("User must have a shipping address before placing an order");
        }

        Order order = new Order(request.code(), effectiveUserId, shippingAddress);
        request.items().forEach(item -> {
            OrderItem orderItem = new OrderItem(item.sku(), item.quantity(), item.priceAtOrder());
            if (item.warehouseId() != null) {
                orderItem.assignWarehouse(item.warehouseId());
            }
            order.addItem(orderItem);
        });

        Order saved = orderRepository.save(order);
        eventPublisher.publishOrderPlaced(new OrderPlacedEvent(
            saved.getId(),
            saved.getUserId(),
            shippingAddress,
            saved.getItems().stream()
                .map(item -> new OrderPlacedEvent.OrderLine(item.getSku(), item.getQuantity(), item.getWarehouseId()))
                .toList(),
            Instant.now()));

        ordersCreatedCounter.increment();

        return OrderMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long id, JwtAuthenticatedUser principal) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        requireCustomerOwns(order.getUserId(), principal);
        return OrderMapper.toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrders(Long userId, OrderStatus status, JwtAuthenticatedUser principal) {
        userId = customerUserIdOrRequested(userId, principal);
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
    public OrderResponseDto updateItems(Long id, OrderItemsUpdateRequestDto request, JwtAuthenticatedUser principal) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        requireCustomerOwns(order.getUserId(), principal);

        List<OrderItem> replacementItems = request.items().stream()
            .map(item -> {
                OrderItem orderItem = new OrderItem(item.sku(), item.quantity(), item.priceAtOrder());
                if (item.warehouseId() != null) {
                    orderItem.assignWarehouse(item.warehouseId());
                }
                return orderItem;
            })
            .toList();

        order.replaceItems(replacementItems);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponseDto updateStatus(Long id, OrderStatus status, JwtAuthenticatedUser principal) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(status);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponseDto assignWorker(Long id, Long workerId, JwtAuthenticatedUser principal) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.assignWorker(workerId);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    private Long customerUserIdOrRequested(Long requestedUserId, JwtAuthenticatedUser principal) {
        if (isCustomer(principal)) {
            if (principal.userId() == null) {
                throw new SecurityException("Authenticated customer scope is missing");
            }
            if (requestedUserId != null && !requestedUserId.equals(principal.userId())) {
                throw new SecurityException("Customers can only access their own orders");
            }
            return principal.userId();
        }
        return requestedUserId;
    }

    private void requireCustomerOwns(Long resourceUserId, JwtAuthenticatedUser principal) {
        if (isCustomer(principal) && (principal.userId() == null || !principal.userId().equals(resourceUserId))) {
            throw new SecurityException("Customers can only access their own orders");
        }
    }

    private boolean isCustomer(JwtAuthenticatedUser principal) {
        return principal != null && "CUSTOMER".equals(principal.role());
    }

}
