package com.kml.services.warehouse.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record WarehouseRequestDto(
    @NotNull Long ownerUserId,
    @NotBlank String name,
    @NotBlank String address,
    List<@Valid StorageUnitRequestDto> storageUnits) {
}
