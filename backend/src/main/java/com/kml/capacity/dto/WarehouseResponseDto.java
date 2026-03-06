package com.kml.capacity.dto;

import java.time.LocalDateTime;

public record WarehouseResponseDto(
    Long id, String name, String address, LocalDateTime createdAt, LocalDateTime updatedAt) {}
