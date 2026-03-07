package com.kml.capacity.mapper;

import java.util.Collections;
import java.util.List;

import com.kml.capacity.dto.ShipmentHistoryResponseDto;
import com.kml.domain.shipment.ShipmentHistory;

public final class ShipmentHistoryMapper {

  private ShipmentHistoryMapper() {}

  public static ShipmentHistoryResponseDto toDto(ShipmentHistory entity) {
    if (entity == null) return null;

    return new ShipmentHistoryResponseDto(
        entity.getId(),
        entity.getPreviousStatus(),
        entity.getNewStatus(),
        entity.getCreatedAt() // use AuditableEntity timestamp
        );
  }

  public static List<ShipmentHistoryResponseDto> toDtoList(List<ShipmentHistory> entities) {
    if (entities == null || entities.isEmpty()) {
      return Collections.emptyList();
    }
    return entities.stream().map(ShipmentHistoryMapper::toDto).toList();
  }
}
