package com.kml.capacity.service;

import com.kml.capacity.dto.ai.ShipmentSimulationEventDto;

public interface SimulationService {

  void startSimulation(Long shipmentId);

  void processSimulationEvent(ShipmentSimulationEventDto event);

  ShipmentSimulationEventDto getSimulationState(Long shipmentId);
}
