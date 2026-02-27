package com.kml.capacity.mapper;

import com.kml.capacity.dto.ShipmentResponseDto;
import com.kml.domain.shipment.Shipment;

public final class ShipmentMapper {

  private ShipmentMapper() {}

  public static ShipmentResponseDto toDto(Shipment entity) {
    if (entity == null) {
      return null;
    }

    ShipmentResponseDto dto = new ShipmentResponseDto();
    dto.setId(entity.getId());
    dto.setTracking(entity.getTracking());
    dto.setCarrierInfo(entity.getCarrierInfo());
    dto.setAddress(entity.getAddress());
    dto.setStatus(entity.getStatus());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setUpdatedAt(entity.getUpdatedAt());

    dto.setOrderId(entity.getOrder() != null ? entity.getOrder().getId() : null);

    return dto;
  }
}
