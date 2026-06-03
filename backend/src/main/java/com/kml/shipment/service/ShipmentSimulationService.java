package com.kml.shipment.service;

import com.kml.shipment.dto.ShipmentSimulationEventDto;

public interface ShipmentSimulationService {

  void startSimulation(Long shipmentId);

  void processSimulationEvent(ShipmentSimulationEventDto event);

  ShipmentSimulationEventDto getSimulationState(Long shipmentId);
}
