package com.kml.capacity.service;

import com.kml.capacity.dto.ai.RoutePlanDto;
import com.kml.capacity.dto.ai.RoutePredictionInputDto;
import com.kml.capacity.dto.ai.ShipmentSimulationEventDto;

public interface RoutingService {

  RoutePlanDto predictRoute(RoutePredictionInputDto input);

  RoutePlanDto adjustRoute(Long shipmentId, ShipmentSimulationEventDto event);
}
