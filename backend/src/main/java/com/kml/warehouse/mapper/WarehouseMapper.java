package com.kml.warehouse.mapper;

import com.kml.warehouse.dto.WarehouseResponseDto;
import com.kml.warehouse.entity.Warehouse;

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
