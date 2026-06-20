package com.kml.services.shipment.dto;

import com.kml.services.shipment.entity.ShipmentStatus;
import java.time.LocalDateTime;

public record ShipmentResponseDto(
    Long id,
    Long orderId,
    Long warehouseId,
    Long userId,
    String trackingCode,
    String address,
    String carrierInfo,
    ShipmentStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long version) {
}
