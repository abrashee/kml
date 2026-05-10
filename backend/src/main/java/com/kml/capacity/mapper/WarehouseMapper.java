package com.kml.capacity.mapper;

import com.kml.capacity.dto.warehouse.WarehouseResponseDto;
import com.kml.domain.warehouse.Warehouse;

public final class WarehouseMapper {

  private WarehouseMapper() {}

  public static WarehouseResponseDto toDto(Warehouse entity) {
    if (entity == null) return null;

    return new WarehouseResponseDto(
        entity.getId(),
        entity.getName() != null ? entity.getName() : "",
        entity.getAddress() != null ? entity.getAddress() : "",
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
