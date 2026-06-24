package com.kml.services.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderItemsUpdateRequestDto(
    @NotEmpty List<@Valid OrderItemRequestDto> items) {
}
