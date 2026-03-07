package com.kml.capacity.dto;

import java.time.LocalDateTime;

import com.kml.domain.shipment.ShipmentStatus;

public record ShipmentResponseDto(
    Long id,
    String tracking,
    String carrierInfo,
    String address,
    ShipmentStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long orderId) {}
