package com.kml.api;

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

import com.kml.capacity.dto.ShipmentHistoryResponseDto;
import com.kml.capacity.dto.ShipmentRequestDto;
import com.kml.capacity.dto.ShipmentResponseDto;
import com.kml.capacity.dto.ShipmentStatusUpdateRequestDto;
import com.kml.capacity.service.ShipmentHistoryService;
import com.kml.capacity.service.ShipmentService;

import jakarta.validation.Valid;

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
        this.shipmentService.createShipment(
            requestDto.getOrderId(), requestDto.getAddress(), requestDto.getCarrierInfo());

    return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER','CUSTOMER')")
  @GetMapping
  public ResponseEntity<List<ShipmentResponseDto>> getAllShipments() {
    return ResponseEntity.ok(this.shipmentService.getAllShipments());
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER','CUSTOMER')")
  @GetMapping("/{id}")
  public ResponseEntity<ShipmentResponseDto> getShipmentById(@PathVariable Long id) {
    return ResponseEntity.ok(this.shipmentService.getShipmentById(id));
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER','CUSTOMER')")
  @GetMapping("/by-status")
  public ResponseEntity<List<ShipmentResponseDto>> getShipmentsByStatus(
      @RequestParam String status) {
    return ResponseEntity.ok(this.shipmentService.getShipmentsByStatus(status));
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER','CUSTOMER')")
  @GetMapping("/by-order")
  public ResponseEntity<List<ShipmentResponseDto>> getShipmentsByOrder(
      @RequestParam("orderId") Long id) {
    return ResponseEntity.ok(this.shipmentService.getShipmentsByOrder(id));
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @PatchMapping("/{id}/status")
  public ResponseEntity<ShipmentResponseDto> updateShipmentStatus(
      @PathVariable Long id, @RequestBody @Valid ShipmentStatusUpdateRequestDto requestDto) {

    return ResponseEntity.ok(shipmentService.updateShipmentStatus(id, requestDto.getStatus()));
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER','CUSTOMER')")
  @GetMapping("/{id}/history")
  public ResponseEntity<List<ShipmentHistoryResponseDto>> getShipmentHistory(
      @PathVariable Long id) {
    return ResponseEntity.ok(shipmentHistoryService.getHistoryForShipment(id));
  }
}
