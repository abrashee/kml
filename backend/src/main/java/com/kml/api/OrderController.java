package com.kml.api;

import java.util.List;

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

import com.kml.capacity.dto.OrderRequestDto;
import com.kml.capacity.dto.OrderResponseDto;
import com.kml.capacity.security.AuthorizationService;
import com.kml.capacity.security.HardcodedUserContext;
import com.kml.capacity.service.OrderService;
import com.kml.domain.user.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  public ResponseEntity<OrderResponseDto> createOrder(
      @RequestBody @Valid OrderRequestDto requestDto) {

    User currentUser = HardcodedUserContext.getCurrentUser();
    if (!AuthorizationService.canCreateOrder(currentUser)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    OrderResponseDto order =
        this.orderService.createOrder(
            requestDto.getCode(), requestDto.getStatusId(), requestDto.getItems());
    return ResponseEntity.status(HttpStatus.CREATED).body(order);
  }

  @GetMapping
  public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
    List<OrderResponseDto> responseDtos = orderService.getAllOrders();
    return ResponseEntity.ok(responseDtos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
    OrderResponseDto order = this.orderService.getOrderById(id);
    return ResponseEntity.ok(order);
  }

  @PutMapping("/{id}")
  public ResponseEntity<OrderResponseDto> updateOrder(
      @PathVariable Long id, @RequestBody @Valid OrderRequestDto requestDto) {

    User currentUser = HardcodedUserContext.getCurrentUser();
    if (!AuthorizationService.canCreateOrder(currentUser)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    OrderResponseDto updatedOrder =
        orderService.updateOrder(id, requestDto.getStatusId(), requestDto.getItems());
    return ResponseEntity.ok(updatedOrder);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    User currentUser = HardcodedUserContext.getCurrentUser();
    if (!AuthorizationService.canCreateOrder(currentUser)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}
