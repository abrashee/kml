package com.kml.services.common.events;

import java.time.Instant;
import java.util.List;

public record OrderPlacedEvent(
    Long orderId,
    Long userId,
    String shippingAddress,
    List<OrderLine> lines,
    Instant occurredAt) {

    public record OrderLine(String sku, int quantity) {
    }
}
