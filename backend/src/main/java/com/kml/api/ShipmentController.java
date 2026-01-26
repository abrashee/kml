package com.kml.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kml.capacity.dto.ShipmentRequestDto;
import com.kml.capacity.dto.ShipmentResponseDto;
import com.kml.capacity.security.AuthorizationService;
import com.kml.capacity.security.HardcodedUserContext;
import com.kml.capacity.service.ShipmentService;
import com.kml.domain.shipment.Shipment;
import com.kml.domain.user.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {
  private final ShipmentService shipmentService;

  public ShipmentController(ShipmentService shipmentService) {
    this.shipmentService = shipmentService;
  }

  @PostMapping
  public ResponseEntity<ShipmentResponseDto> createShipment(
      @RequestBody @Valid ShipmentRequestDto requestDto) {
    User currentUser = HardcodedUserContext.getCurrentUser();
    if (!AuthorizationService.canCreateShipment(currentUser)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    Shipment shipment =
        this.shipmentService.createShipment(
            requestDto.getOrderId(), requestDto.getAddress(), requestDto.getCarrierInfo());

    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(shipment));
  }

  @GetMapping
  public ResponseEntity<List<ShipmentResponseDto>> getAllShipments() {
    List<ShipmentResponseDto> responseDtos =
        this.shipmentService.getAllShipments().stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());

    return ResponseEntity.ok(responseDtos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ShipmentResponseDto> getShipmentById(@PathVariable Long id) {
    return ResponseEntity.ok(mapToResponseDto(this.shipmentService.getShipmentById(id)));
  }

  // Get by status
  @GetMapping("/by-status")
  public ResponseEntity<List<ShipmentResponseDto>> getShipmentsByStatus(
      @RequestParam String status) {
    List<ShipmentResponseDto> responseDtos =
        this.shipmentService.getShipmentsByStatus(status).stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());

    return ResponseEntity.ok(responseDtos);
  }

  // get by order
  @GetMapping("/by-order")
  public ResponseEntity<List<ShipmentResponseDto>> getShipmentsByOrder(
      @RequestParam("orderId") Long id) {
    List<ShipmentResponseDto> responseDtos =
        this.shipmentService.getShipmentsByOrder(id).stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());

    return ResponseEntity.ok(responseDtos);
  }

  private ShipmentResponseDto mapToResponseDto(Shipment shipment) {
    ShipmentResponseDto responseDto = new ShipmentResponseDto();
    responseDto.setId(shipment.getId());
    responseDto.setTracking(shipment.getTracking());
    responseDto.setCarrierInfo(shipment.getCarrierInfo());
    responseDto.setAddress(shipment.getAddress());
    responseDto.setStatus(shipment.getStatus());
    responseDto.setCreatedAt(shipment.getCreatedAt());
    responseDto.setUpdatedAt(shipment.getUpdatedAt());
    responseDto.setOrderId(shipment.getOrder().getId());
    return responseDto;
  }
}
