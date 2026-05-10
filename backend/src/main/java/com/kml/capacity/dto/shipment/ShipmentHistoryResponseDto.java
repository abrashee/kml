package com.kml.capacity.dto.shipment;

import com.kml.domain.shipment.ShipmentStatus;
import java.time.LocalDateTime;

public record ShipmentHistoryResponseDto(
    Long id, ShipmentStatus previousStatus, ShipmentStatus newStatus, LocalDateTime createdAt) {}
