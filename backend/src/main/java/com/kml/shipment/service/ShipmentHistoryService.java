package com.kml.shipment.service;

import java.util.List;

import com.kml.shipment.dto.ShipmentHistoryResponseDto;

public interface ShipmentHistoryService {
  List<ShipmentHistoryResponseDto> getHistoryForShipment(Long shipmentId);
}
