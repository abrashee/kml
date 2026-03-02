package com.kml.capacity.service;

import java.util.List;

import com.kml.capacity.dto.ShipmentHistoryResponseDto;

public interface ShipmentHistoryService {
  List<ShipmentHistoryResponseDto> getHistoryForShipment(Long shipmentId);
}
