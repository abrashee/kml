package com.kml.services.shipment.service;

import com.kml.services.shipment.dto.ShipmentRequestDto;
import com.kml.services.shipment.dto.ShipmentResponseDto;
import com.kml.services.shipment.entity.ShipmentStatus;
import java.util.List;

public interface ShipmentService {

    ShipmentResponseDto createShipment(ShipmentRequestDto request);

    ShipmentResponseDto getShipment(Long id);

    List<ShipmentResponseDto> getShipments(Long orderId, Long userId, ShipmentStatus status);

    ShipmentResponseDto updateStatus(Long id, ShipmentStatus status);
}
