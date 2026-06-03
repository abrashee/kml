package com.kml.shipment.dto;

import java.time.LocalDateTime;

import com.kml.shipment.entity.ShipmentStatus;

public record ShipmentHistoryResponseDto(
    Long id, ShipmentStatus previousStatus, ShipmentStatus newStatus, LocalDateTime createdAt) {}
