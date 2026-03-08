package com.kml.capacity.dto.order;

import java.math.BigDecimal;

public record OrderItemResponseDto(
    Long id, Long inventoryItemId, int quantity, BigDecimal priceAtOrder) {}
