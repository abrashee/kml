package com.kml.services.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryQuantityUpdateRequestDto(
    @NotNull
    @Min(1)
    Integer delta) {
}
