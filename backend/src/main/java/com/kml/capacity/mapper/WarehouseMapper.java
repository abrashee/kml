package com.kml.capacity.mapper;

import com.kml.capacity.dto.WarehouseResponseDto;
import com.kml.domain.warehouse.Warehouse;

public final class WarehouseMapper {

  private WarehouseMapper() {}

  public static WarehouseResponseDto toDto(Warehouse entity) {
    if (entity == null) return null;
    return new WarehouseResponseDto(entity.getId(), entity.getName(), entity.getAddress());
  }
}
