package com.kml.capacity.mapper;

import com.kml.capacity.dto.ShipmentHistoryResponseDto;
import com.kml.domain.shipment.ShipmentHistory;
import java.util.List;

public final class ShipmentHistoryMapper {

  private ShipmentHistoryMapper() {}

  public static ShipmentHistoryResponseDto toDto(ShipmentHistory entity) {
    if (entity == null) return null;

    return new ShipmentHistoryResponseDto(
        entity.getId(), entity.getPreviousStatus(), entity.getNewStatus(), entity.getChangedAt());
  }

  public static List<ShipmentHistoryResponseDto> toDtoList(List<ShipmentHistory> entities) {
    return entities.stream().map(ShipmentHistoryMapper::toDto).toList();
  }
}
