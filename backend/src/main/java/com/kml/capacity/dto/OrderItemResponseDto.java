package com.kml.capacity.dto;

import java.math.BigDecimal;

public record OrderItemResponseDto(
    Long id, Long inventoryItemId, int quantity, BigDecimal priceAtOrder) {}
