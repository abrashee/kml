package com.kml.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RestController;

import com.kml.capacity.dto.OrderStatusRequestDto;
import com.kml.capacity.dto.OrderStatusResponseDto;
import com.kml.capacity.mapper.OrderStatusMapper;
import com.kml.capacity.service.OrderStatusService;
import com.kml.domain.order.OrderStatus;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/order-statuses")
public class OrderStatusController {

  private final OrderStatusService orderStatusService;

  public OrderStatusController(OrderStatusService orderStatusService) {
    this.orderStatusService = orderStatusService;
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @PostMapping
  public ResponseEntity<OrderStatusResponseDto> createOrderStatus(
      @RequestBody @Valid OrderStatusRequestDto requestDto) {
    OrderStatus created =
        orderStatusService.createOrderStatus(requestDto.getName(), requestDto.getDescription());
    return ResponseEntity.status(HttpStatus.CREATED).body(OrderStatusMapper.toDto(created));
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping
  public ResponseEntity<List<OrderStatusResponseDto>> getAllOrderStatuses() {
    List<OrderStatusResponseDto> dtos =
        orderStatusService.getAllOrderStatuses().stream()
            .map(OrderStatusMapper::toDto)
            .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/name/{name}")
  public ResponseEntity<OrderStatusResponseDto> getByName(@PathVariable String name) {
    Optional<OrderStatus> orderStatus = orderStatusService.getByName(name);
    return orderStatus
        .map(status -> ResponseEntity.ok(OrderStatusMapper.toDto(status)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @PutMapping("/{id}")
  public ResponseEntity<OrderStatusResponseDto> updateOrderStatus(
      @PathVariable Long id, @RequestBody @Valid OrderStatusRequestDto requestDto) {
    OrderStatus updated =
        orderStatusService.updateOrderStatus(id, requestDto.getName(), requestDto.getDescription());
    return ResponseEntity.ok(OrderStatusMapper.toDto(updated));
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrderStatus(@PathVariable Long id) {
    orderStatusService.deleteOrderStatus(id);
    return ResponseEntity.noContent().build();
  }
}
