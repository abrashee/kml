package com.kml.services.shipment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShipmentRequestDto(
    @NotNull Long orderId,
    @NotNull Long warehouseId,
    @NotNull Long userId,
    @NotBlank String address,
    String carrierInfo) {
}
