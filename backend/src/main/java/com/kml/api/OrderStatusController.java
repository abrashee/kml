package com.kml.api;

import java.util.List;

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
import com.kml.capacity.security.CurrentUserProvider;
import com.kml.capacity.service.OrderStatusService;
import com.kml.domain.user.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/order-statuses")
public class OrderStatusController {

  private final OrderStatusService orderStatusService;
  private final CurrentUserProvider currentUserProvider;

  public OrderStatusController(
      OrderStatusService orderStatusService, CurrentUserProvider currentUserProvider) {
    this.orderStatusService = orderStatusService;
    this.currentUserProvider = currentUserProvider;
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @PostMapping
  public ResponseEntity<OrderStatusResponseDto> createOrderStatus(
      @RequestBody @Valid OrderStatusRequestDto requestDto) {

    User currentUser = currentUserProvider.getCurrentUser();

    OrderStatusResponseDto created =
        orderStatusService.createOrderStatus(
            currentUser, requestDto.getName(), requestDto.getDescription());

    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping
  public ResponseEntity<List<OrderStatusResponseDto>> getAllOrderStatuses() {
    List<OrderStatusResponseDto> dtos = orderStatusService.getAllOrderStatuses();
    return ResponseEntity.ok(dtos);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/name/{name}")
  public ResponseEntity<OrderStatusResponseDto> getByName(@PathVariable String name) {
    OrderStatusResponseDto dto = orderStatusService.getByName(name);
    if (dto == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @PutMapping("/{id}")
  public ResponseEntity<OrderStatusResponseDto> updateOrderStatus(
      @PathVariable Long id, @RequestBody @Valid OrderStatusRequestDto requestDto) {

    OrderStatusResponseDto updated =
        orderStatusService.updateOrderStatus(id, requestDto.getName(), requestDto.getDescription());

    return ResponseEntity.ok(updated);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrderStatus(@PathVariable Long id) {
    orderStatusService.deleteOrderStatus(id);
    return ResponseEntity.noContent().build();
  }
}
