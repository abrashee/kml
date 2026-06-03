package com.kml.order.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kml.order.dto.OrderRequestDto;
import com.kml.order.dto.OrderResponseDto;
import com.kml.order.service.OrderService;
import com.kml.security.CurrentUserProvider;
import com.kml.user.entity.User;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Orders")
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;
  private final CurrentUserProvider currentUserProvider;

  public OrderController(OrderService orderService, CurrentUserProvider currentUserProvider) {
    this.orderService = orderService;
    this.currentUserProvider = currentUserProvider;
  }

  @PreAuthorize("hasAnyRole('USER','CUSTOMER')")
  @PostMapping
  public ResponseEntity<OrderResponseDto> createOrder(
      @RequestBody @Valid OrderRequestDto requestDto) {

    User currentUser = currentUserProvider.getCurrentUser();

    OrderResponseDto order =
        orderService.createOrder(
            requestDto.getCode(), requestDto.getStatusId(), requestDto.getItems(), currentUser);

    return ResponseEntity.status(HttpStatus.CREATED).body(order);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER','CUSTOMER')")
  @GetMapping
  public ResponseEntity<Page<OrderResponseDto>> getAllOrders(
      @RequestParam(required = false) Boolean list,
      @PageableDefault(size = 20) Pageable pageable) {

    if (Boolean.TRUE.equals(list)) {
      List<OrderResponseDto> orders = orderService.getAllOrders();
      return ResponseEntity.ok(
          new org.springframework.data.domain.PageImpl<>(orders));
    }
    Page<OrderResponseDto> orders = orderService.getAllOrdersPage(pageable);
    return ResponseEntity.ok(orders);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER','CUSTOMER')")
  @GetMapping("/{id}")
  public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {

    OrderResponseDto order = orderService.getOrderById(id);

    return ResponseEntity.ok(order);
  }

  @PreAuthorize("hasAnyRole('USER','CUSTOMER')")
  @PutMapping("/{id}")
  public ResponseEntity<OrderResponseDto> updateOrder(
      @PathVariable Long id, @RequestBody @Valid OrderRequestDto requestDto) {

    User currentUser = currentUserProvider.getCurrentUser();

    OrderResponseDto updatedOrder =
        orderService.updateOrder(id, requestDto.getStatusId(), requestDto.getItems(), currentUser);

    return ResponseEntity.ok(updatedOrder);
  }

  @PreAuthorize("hasAnyRole('USER','CUSTOMER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {

    orderService.deleteOrder(id);

    return ResponseEntity.noContent().build();
  }
}

