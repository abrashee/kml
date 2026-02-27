package com.kml.capacity.mapper;

import com.kml.capacity.dto.WarehouseResponseDto;
import com.kml.domain.warehouse.Warehouse;

public final class WarehouseMapper {

  private WarehouseMapper() {}

  public static WarehouseResponseDto toDto(Warehouse entity) {
    WarehouseResponseDto dto = new WarehouseResponseDto();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    dto.setAddress(entity.getAddress());
    return dto;
  }
}
