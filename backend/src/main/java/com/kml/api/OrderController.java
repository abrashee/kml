package com.kml.api;

import com.kml.capacity.dto.OrderItemResponseDto;
import com.kml.capacity.dto.OrderRequestDto;
import com.kml.capacity.dto.OrderResponseDto;
import com.kml.capacity.service.OrderService;
import com.kml.capacity.service.OrderStatusService;
import com.kml.domain.order.Order;
import com.kml.domain.order.OrderItem;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;
  private final OrderStatusService orderStatusService;

  public OrderController(OrderService orderService, OrderStatusService orderStatusService) {
    this.orderService = orderService;
    this.orderStatusService = orderStatusService;
  }

  @PostMapping
  public ResponseEntity<OrderResponseDto> createOrder(
      @RequestBody @Valid OrderRequestDto requestDto) {

    Order order =
        this.orderService.createOrder(
            requestDto.getCode(), requestDto.getStatusId(), requestDto.getItems());
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(order));
  }

  @GetMapping
  public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
    List<OrderResponseDto> responseDtos =
        orderService.getAllOrders().stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    return ResponseEntity.ok(responseDtos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
    return ResponseEntity.ok(mapToResponseDto(orderService.getOrderById(id)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<OrderResponseDto> updateOrder(
      @PathVariable Long id, @RequestBody @Valid OrderRequestDto requestDto) {

    Order updatedOrder =
        orderService.updateOrder(id, requestDto.getStatusId(), requestDto.getItems());
    return ResponseEntity.ok(mapToResponseDto(updatedOrder));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }

  private OrderResponseDto mapToResponseDto(Order order) {

    OrderResponseDto dto = new OrderResponseDto();
    dto.setId(order.getId());
    dto.setCode(order.getCode());
    dto.setStatusName(order.getStatus().getName());
    dto.setCreatedAt(order.getCreatedAt());
    dto.setUpdatedAt(order.getUpdatedAt());
    dto.setItems(
        order.getItems().stream().map(this::mapToResponseDto).collect(Collectors.toList()));
    return dto;
  }

  private OrderItemResponseDto mapToResponseDto(OrderItem item) {
    OrderItemResponseDto dto = new OrderItemResponseDto();
    dto.setInventoryItemId(item.getInventoryItem().getId());
    dto.setQuantity(item.getQuantity());
    dto.setPriceAtOrder(item.getPriceAtOrder());

    return dto;
  }
}
