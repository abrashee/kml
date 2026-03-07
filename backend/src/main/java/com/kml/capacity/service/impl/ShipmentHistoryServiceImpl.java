package com.kml.capacity.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kml.capacity.dto.ShipmentHistoryResponseDto;
import com.kml.capacity.mapper.ShipmentHistoryMapper;
import com.kml.capacity.service.ShipmentHistoryService;
import com.kml.infra.ShipmentHistoryRepository;

@Service
public class ShipmentHistoryServiceImpl implements ShipmentHistoryService {

  private final ShipmentHistoryRepository shipmentHistoryRepository;

  public ShipmentHistoryServiceImpl(ShipmentHistoryRepository shipmentHistoryRepository) {
    this.shipmentHistoryRepository = shipmentHistoryRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ShipmentHistoryResponseDto> getHistoryForShipment(Long shipmentId) {
    if (shipmentId == null) {
      throw new IllegalArgumentException("ShipmentId is required");
    }

    return shipmentHistoryRepository.findByShipment_IdOrderByCreatedAtAsc(shipmentId).stream()
        .map(sh -> ShipmentHistoryMapper.toDto(sh))
        .toList();
  }
}
