package com.kml.api;

import com.kml.capacity.dto.ShipmentRequestDto;
import com.kml.capacity.dto.ShipmentResponseDto;
import com.kml.capacity.dto.ShipmentStatusUpdateRequestDto;
import com.kml.capacity.service.ShipmentService;
import com.kml.domain.shipment.Shipment;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    Shipment shipment =
        this.shipmentService.createShipment(
            requestDto.getOrderId(), requestDto.getAddress(), requestDto.getCarrierInfo());

    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(shipment));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<ShipmentResponseDto> updateShipment(
      @PathVariable Long id, @RequestBody @Valid ShipmentStatusUpdateRequestDto requestDto) {
    Shipment shipment = this.shipmentService.updateShipmentStatus(id, requestDto.getStatus());

    return ResponseEntity.ok(mapToResponseDto(shipment));
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
