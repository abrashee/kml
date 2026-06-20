package com.kml.services.order.dto;

import java.math.BigDecimal;

public record OrderItemResponseDto(
    Long id,
    String sku,
    int quantity,
    BigDecimal priceAtOrder,
    Long warehouseId) {
}
