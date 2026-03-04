package com.kml.capacity.dto;

import java.time.LocalDateTime;

import com.kml.domain.shipment.ShipmentStatus;

public record ShipmentHistoryResponseDto(
    Long id, ShipmentStatus previousStatus, ShipmentStatus newStatus, LocalDateTime changedAt) {}
