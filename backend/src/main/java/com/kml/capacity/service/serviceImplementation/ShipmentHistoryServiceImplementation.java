package com.kml.capacity.service.serviceImplementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kml.capacity.dto.ShipmentHistoryResponseDto;
import com.kml.capacity.mapper.ShipmentHistoryMapper;
import com.kml.capacity.service.ShipmentHistoryService;
import com.kml.infra.ShipmentHistoryRepository;

@Service
public class ShipmentHistoryServiceImplementation implements ShipmentHistoryService {

  private final ShipmentHistoryRepository shipmentHistoryRepository;

  public ShipmentHistoryServiceImplementation(ShipmentHistoryRepository shipmentHistoryRepository) {
    this.shipmentHistoryRepository = shipmentHistoryRepository;
  }

  @Override
  public List<ShipmentHistoryResponseDto> getHistoryForShipment(Long shipmentId) {
    if (shipmentId == null) {
      throw new IllegalArgumentException("ShipmentId is required");
    }

    return shipmentHistoryRepository.findByShipment_IdOrderByChangedAtAsc(shipmentId).stream()
        .map(ShipmentHistoryMapper::toDto)
        .toList();
  }
}
