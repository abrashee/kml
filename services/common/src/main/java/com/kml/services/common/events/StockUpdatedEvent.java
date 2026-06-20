package com.kml.services.common.events;

import java.time.Instant;

public record StockUpdatedEvent(
    Long inventoryItemId,
    String sku,
    int quantity,
    Long warehouseId,
    Instant occurredAt) {
}
