package com.kml.services.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record StorageUnitRequestDto(
    @NotBlank String code,
    @Min(1) int capacity) {
}
