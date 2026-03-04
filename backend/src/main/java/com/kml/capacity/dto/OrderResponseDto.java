package com.kml.capacity.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
    Long id,
    String code,
    Long statusId,
    String statusName,
    Long userId,
    String username,
    List<OrderItemResponseDto> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
