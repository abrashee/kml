package com.kml.capacity.mapper;

import com.kml.capacity.dto.ShipmentResponseDto;
import com.kml.domain.shipment.Shipment;
import java.util.List;

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
    return entities.stream().map(ShipmentMapper::toDto).toList();
  }
}
