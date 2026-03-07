package com.kml.api;

import com.kml.capacity.dto.ShipmentHistoryResponseDto;
import com.kml.capacity.dto.ShipmentRequestDto;
import com.kml.capacity.dto.ShipmentResponseDto;
import com.kml.capacity.dto.ShipmentStatusUpdateRequestDto;
import com.kml.capacity.service.ShipmentHistoryService;
import com.kml.capacity.service.ShipmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

  private final ShipmentService shipmentService;
  private final ShipmentHistoryService shipmentHistoryService;

  public ShipmentController(
      ShipmentService shipmentService, ShipmentHistoryService shipmentHistoryService) {
    this.shipmentService = shipmentService;
    this.shipmentHistoryService = shipmentHistoryService;
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @PostMapping
  public ResponseEntity<ShipmentResponseDto> createShipment(
      @RequestBody @Valid ShipmentRequestDto requestDto) {

    ShipmentResponseDto shipment =
        shipmentService.createShipment(
            requestDto.getOrderId(), requestDto.getAddress(), requestDto.getCarrierInfo());

    return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER','CUSTOMER')")
  @GetMapping
  public ResponseEntity<List<ShipmentResponseDto>> getShipments(
      @RequestParam(required = false) String status, @RequestParam(required = false) Long orderId) {

    List<ShipmentResponseDto> shipments;

    if (status != null) {
      shipments = shipmentService.getShipmentsByStatus(status);
    } else if (orderId != null) {
      shipments = shipmentService.getShipmentsByOrder(orderId);
    } else {
      shipments = shipmentService.getAllShipments();
    }

    return ResponseEntity.ok(shipments);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER','CUSTOMER')")
  @GetMapping("/{id}")
  public ResponseEntity<ShipmentResponseDto> getShipment(@PathVariable Long id) {
    return ResponseEntity.ok(shipmentService.getShipmentById(id));
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @PatchMapping("/{id}/status")
  public ResponseEntity<ShipmentResponseDto> updateShipmentStatus(
      @PathVariable Long id, @RequestBody @Valid ShipmentStatusUpdateRequestDto requestDto) {

    ShipmentResponseDto updated = shipmentService.updateShipmentStatus(id, requestDto.getStatus());

    return ResponseEntity.ok(updated);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER','CUSTOMER')")
  @GetMapping("/{id}/history")
  public ResponseEntity<List<ShipmentHistoryResponseDto>> getShipmentHistory(
      @PathVariable Long id) {

    List<ShipmentHistoryResponseDto> history = shipmentHistoryService.getHistoryForShipment(id);

    return ResponseEntity.ok(history);
  }
}
