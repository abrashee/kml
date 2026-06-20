package com.kml.services.common.events;

import java.time.Instant;
import java.util.List;

public record InventoryReservedEvent(
    Long orderId,
    Long userId,
    String shippingAddress,
    Long warehouseId,
    List<ReservationLine> lines,
    Instant occurredAt) {

    public record ReservationLine(String sku, int quantity) {
    }
}
