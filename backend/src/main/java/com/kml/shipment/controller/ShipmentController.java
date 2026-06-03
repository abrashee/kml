package com.kml.shipment.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.kml.shipment.dto.ShipmentHistoryResponseDto;
import com.kml.shipment.dto.ShipmentRequestDto;
import com.kml.shipment.dto.ShipmentResponseDto;
import com.kml.shipment.dto.ShipmentStatusUpdateRequestDto;
import com.kml.shipment.service.ShipmentHistoryService;
import com.kml.shipment.service.ShipmentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Shipments")
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
  public ResponseEntity<Page<ShipmentResponseDto>> getShipments(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) Long orderId,
      @PageableDefault(size = 20) Pageable pageable) {

    if (status != null || orderId != null) {
      List<ShipmentResponseDto> shipments;
      if (status != null) {
        shipments = shipmentService.getShipmentsByStatus(status);
      } else {
        shipments = shipmentService.getShipmentsByOrder(orderId);
      }
      return ResponseEntity.ok(
          new org.springframework.data.domain.PageImpl<>(shipments));
    }
    return ResponseEntity.ok(shipmentService.getShipmentsPage(pageable));
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

