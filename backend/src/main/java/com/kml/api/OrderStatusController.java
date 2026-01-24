package com.kml.api;

import com.kml.capacity.dto.OrderStatusRequestDto;
import com.kml.capacity.dto.OrderStatusResponseDto;
import com.kml.capacity.service.OrderStatusService;
import com.kml.domain.order.OrderStatus;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
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
@RequestMapping("/api/v1/order-statues")
public class OrderStatusController {
  private final OrderStatusService orderStatusService;

  public OrderStatusController(OrderStatusService orderStatusService) {
    this.orderStatusService = orderStatusService;
  }

  @PostMapping
  public ResponseEntity<OrderStatusResponseDto> createOrderStatus(
      @RequestBody @Valid OrderStatusResponseDto requestDto) {
    OrderStatus orderStatus =
        orderStatusService.createOrderStatus(requestDto.getName(), requestDto.getDescription());
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(orderStatus));
  }

  @GetMapping
  public ResponseEntity<List<OrderStatusResponseDto>> getAllOrderStatues() {
    List<OrderStatusResponseDto> dtos =
        orderStatusService.getAllOrderStatuses().stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());

    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<OrderStatusResponseDto> getByName(@PathVariable String name) {
    Optional<OrderStatus> orderStatus = orderStatusService.getByName(name);

    return orderStatus
        .map(status -> ResponseEntity.ok(mapToResponseDto(status)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<OrderStatusResponseDto> updateOrderStatus(
      @PathVariable Long id, @RequestBody @Valid OrderStatusRequestDto requestDto) {
    OrderStatus updateStatus =
        orderStatusService.updateOrderStatus(id, requestDto.getName(), requestDto.getDescription());

    return ResponseEntity.ok(mapToResponseDto(updateStatus));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrderStatus(@PathVariable Long id) {
    orderStatusService.deleteOrderStatus(id);
    return ResponseEntity.noContent().build();
  }

  private OrderStatusResponseDto mapToResponseDto(OrderStatus orderStatus) {
    OrderStatusResponseDto dto = new OrderStatusResponseDto();
    dto.setId(orderStatus.getId());
    dto.setName(orderStatus.getName());
    dto.setDescription(orderStatus.getDescription());
    dto.setCreatedAt(orderStatus.getCreatedAt());
    dto.setUpdatedAt(orderStatus.getUpdatedAt());
    return dto;
  }
}
