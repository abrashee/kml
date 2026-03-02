package com.kml.capacity.mapper;

import com.kml.capacity.dto.ShipmentHistoryResponseDto;
import com.kml.domain.shipment.ShipmentHistory;

public final class ShipmentHistoryMapper {

  private ShipmentHistoryMapper() {}

  public static ShipmentHistoryResponseDto toDto(ShipmentHistory entity) {
    if (entity == null) {
      return null;
    }

    ShipmentHistoryResponseDto dto = new ShipmentHistoryResponseDto();
    dto.setId(entity.getId());
    dto.setPreviousStatus(entity.getPreviousStatus());
    dto.setNewStatus(entity.getNewStatus());
    dto.setChangedAt(entity.getChangedAt());
    return dto;
  }
}
