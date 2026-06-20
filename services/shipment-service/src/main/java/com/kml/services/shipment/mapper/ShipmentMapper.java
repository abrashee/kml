package com.kml.services.shipment.mapper;

import com.kml.services.shipment.dto.ShipmentResponseDto;
import com.kml.services.shipment.entity.Shipment;

public final class ShipmentMapper {

    private ShipmentMapper() {
    }

    public static ShipmentResponseDto toDto(Shipment shipment) {
        return new ShipmentResponseDto(
            shipment.getId(),
            shipment.getOrderId(),
            shipment.getWarehouseId(),
            shipment.getUserId(),
            shipment.getTrackingCode(),
            shipment.getAddress(),
            shipment.getCarrierInfo(),
            shipment.getStatus(),
            shipment.getCreatedAt(),
            shipment.getUpdatedAt(),
            shipment.getVersion());
    }
}
