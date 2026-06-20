package com.kml.services.common.events;

import java.time.Instant;

public record ShipmentCreatedEvent(
    Long shipmentId,
    Long orderId,
    Long warehouseId,
    String trackingCode,
    Instant occurredAt) {
}
