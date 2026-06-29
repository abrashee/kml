package com.kml.services.shipment.service;

import com.kml.services.common.security.jwt.JwtAuthenticatedUser;
import com.kml.services.shipment.dto.ShipmentRequestDto;
import com.kml.services.shipment.dto.ShipmentResponseDto;
import com.kml.services.shipment.entity.ShipmentStatus;
import java.util.List;

public interface ShipmentService {

    ShipmentResponseDto createShipment(ShipmentRequestDto request);

    ShipmentResponseDto createShipment(ShipmentRequestDto request, JwtAuthenticatedUser principal);

    ShipmentResponseDto getShipment(Long id, JwtAuthenticatedUser principal);

    List<ShipmentResponseDto> getShipments(Long orderId, Long userId, ShipmentStatus status, JwtAuthenticatedUser principal);

    ShipmentResponseDto updateStatus(Long id, ShipmentStatus status, JwtAuthenticatedUser principal);
}
