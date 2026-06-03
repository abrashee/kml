package com.kml.shipment.service;

import com.kml.ai.dto.RoutePlanDto;
import com.kml.ai.dto.RoutePredictionInputDto;
import com.kml.shipment.dto.ShipmentSimulationEventDto;

public interface RoutingService {

  RoutePlanDto predictRoute(RoutePredictionInputDto input);

  RoutePlanDto adjustRoute(Long shipmentId, ShipmentSimulationEventDto event);
}
