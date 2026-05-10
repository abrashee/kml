package com.kml.capacity.mapper;

import java.util.Collections;
import java.util.List;

import com.kml.capacity.dto.shipment.ShipmentResponseDto;
import com.kml.domain.shipment.Shipment;

public final class ShipmentMapper {

  private ShipmentMapper() {}

  public static ShipmentResponseDto toDto(Shipment entity) {
    if (entity == null) return null;

    return new ShipmentResponseDto(
        entity.getId(),
        entity.getTracking(),
        entity.getCarrierInfo(),
        entity.getAddress(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getOrder() != null ? entity.getOrder().getId() : null);
  }

  public static List<ShipmentResponseDto> toDtoList(List<Shipment> entities) {
    if (entities == null || entities.isEmpty()) {
      return Collections.emptyList();
    }
    return entities.stream().map(ShipmentMapper::toDto).toList();
  }
}
