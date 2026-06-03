package com.kml.inventory.dto;

import java.time.LocalDateTime;

public record InventoryItemResponseDto(
    Long id,
    String sku,
    String name,
    int quantity,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
