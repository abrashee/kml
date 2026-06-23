package com.kml.services.inventory.product;

import java.math.BigDecimal;

public record ProductResponseDto(
    Long id,
    String sku,
    String name,
    String description,
    BigDecimal price,
    int quantity,
    Long primaryWarehouseId) {
}
