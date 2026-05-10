package com.kml.capacity.dto.inventory;

import java.time.LocalDateTime;

public record InventoryItemLayoutDto(
    Long id,
    String sku,
    String name,
    int quantity,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
