package com.kml.capacity.service.impl;

import com.kml.capacity.dto.ai.RoutePlanDto;
import com.kml.capacity.dto.ai.RoutePredictionInputDto;
import com.kml.capacity.dto.ai.ShipmentSimulationEventDto;
import com.kml.capacity.service.RoutingService;
import com.kml.capacity.service.SimulationService;
import com.kml.domain.shipment.Shipment;
import com.kml.infra.ShipmentRepository;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SimulationServiceImpl implements SimulationService {

  private static final Logger log = LoggerFactory.getLogger(SimulationServiceImpl.class);

  private final RoutingService routingService;
  private final ShipmentRepository shipmentRepository;

  private final ConcurrentHashMap<Long, ShipmentSimulationEventDto> simulationState =
      new ConcurrentHashMap<>();

  public SimulationServiceImpl(
      RoutingService routingService, ShipmentRepository shipmentRepository) {
    this.routingService = routingService;
    this.shipmentRepository = shipmentRepository;
  }

  @Override
  public void startSimulation(Long shipmentId) {
    Shipment shipment =
        shipmentRepository
            .findById(shipmentId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

    ShipmentSimulationEventDto initialEvent = new ShipmentSimulationEventDto();
    initialEvent.setShipmentId(shipmentId);
    initialEvent.setCurrentLocation("Origin");
    initialEvent.setCurrentLoad(
        shipment.getOrder().getItems().stream().mapToInt(i -> i.getQuantity()).sum());
    initialEvent.setTimestamp(LocalDateTime.now());

    simulationState.put(shipmentId, initialEvent);

    RoutePredictionInputDto input = new RoutePredictionInputDto();
    input.setShipmentId(shipmentId);
    input.setOrigin("Origin");
    input.setDestination(shipment.getAddress());
    input.setShipmentWeight(initialEvent.getCurrentLoad());
    input.setRequestedDeliveryTime(LocalDateTime.now().plusHours(5));

    RoutePlanDto plan;
    try {
      // AI-powered prediction
      plan = routingService.predictRoute(input);
    } catch (Exception e) {
      log.warn(
          "AI route prediction failed for shipmentId={}, falling back to static route",
          shipmentId,
          e);
      // Fallback: call internal static calculation
      plan = routingService.adjustRoute(shipmentId, initialEvent);
    }

    shipment.setLastRoutePlan(plan);
    shipmentRepository.save(shipment);
  }

  @Override
  public void processSimulationEvent(ShipmentSimulationEventDto event) {
    simulationState.put(event.getShipmentId(), event);

    Shipment shipment =
        shipmentRepository
            .findById(event.getShipmentId())
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

    RoutePlanDto adjustedPlan;
    try {
      // AI-powered dynamic rerouting
      adjustedPlan = routingService.adjustRoute(event.getShipmentId(), event);
    } catch (Exception e) {
      log.warn(
          "AI dynamic rerouting failed for shipmentId={}, using static adjustment",
          event.getShipmentId(),
          e);
      // Fallback to static calculation
      adjustedPlan = routingService.adjustRoute(event.getShipmentId(), event);
    }

    // Store AI or fallback adjusted route
    shipment.setLastRoutePlan(adjustedPlan);
    shipmentRepository.save(shipment);
  }

  @Override
  public ShipmentSimulationEventDto getSimulationState(Long shipmentId) {
    return simulationState.get(shipmentId);
  }
}
