package com.kml.capacity.dto.inventory;

import java.time.LocalDateTime;

public record InventoryItemResponseDto(
    Long id,
    String sku,
    String name,
    int quantity,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
