package com.kml.capacity.service.impl;

import com.kml.capacity.dto.ai.RoutePlanDto;
import com.kml.capacity.dto.ai.RoutePredictionInputDto;
import com.kml.capacity.dto.ai.ShipmentSimulationEventDto;
import com.kml.capacity.service.RoutingService;
import com.kml.domain.shipment.Shipment;
import com.kml.domain.warehouse.Warehouse;
import com.kml.infra.ShipmentRepository;
import com.kml.infra.StorageUnitInventoryAssignmentRepository;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class RoutingServiceImpl implements RoutingService {

  private final ShipmentRepository shipmentRepository;
  private final StorageUnitInventoryAssignmentRepository assignmentRepository;

  private final String GEMINI_API_URL = System.getenv("GEMINI_API_URL");
  private final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");

  private static final Random random = new Random();

  public RoutingServiceImpl(
      ShipmentRepository shipmentRepository,
      StorageUnitInventoryAssignmentRepository assignmentRepository) {
    this.shipmentRepository = shipmentRepository;
    this.assignmentRepository = assignmentRepository;
  }

  @Override
  public RoutePlanDto predictRoute(RoutePredictionInputDto input) {
    Shipment shipment =
        shipmentRepository
            .findById(input.getShipmentId())
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

    double weight =
        input.getShipmentWeight() > 0 ? input.getShipmentWeight() : generateRandomWeight();
    LocalDateTime requestedDelivery =
        input.getRequestedDeliveryTime() != null
            ? input.getRequestedDeliveryTime()
            : LocalDateTime.now().plusDays(1);

    RoutePlanDto plan =
        buildRoutePlan(
            shipment, input.getOrigin(), input.getDestination(), weight, requestedDelivery);

    shipment.setLastRoutePlan(plan);
    shipmentRepository.save(shipment);

    return plan;
  }

  @Override
  public RoutePlanDto adjustRoute(Long shipmentId, ShipmentSimulationEventDto event) {
    Shipment shipment =
        shipmentRepository
            .findById(shipmentId)
            .orElseThrow(() -> new IllegalArgumentException("Shipment not found"));

    // Keep dynamic AI context: recent events
    List<ShipmentSimulationEventDto> recentEvents = new ArrayList<>();
    recentEvents.add(event);
    // TODO: optionally fetch previous events for richer context

    double weight = generateRandomWeight();
    LocalDateTime requestedDelivery = event.getTimestamp().plusHours(5);

    RoutePlanDto plan;
    try {
      JSONObject requestJson = new JSONObject();
      requestJson.put("shipmentId", shipment.getId());
      requestJson.put("currentLocation", event.getCurrentLocation());
      requestJson.put("currentLoad", weight);
      requestJson.put("timestamp", event.getTimestamp().toString());

      JSONArray eventsArr = new JSONArray();
      for (ShipmentSimulationEventDto e : recentEvents) {
        JSONObject evt = new JSONObject();
        evt.put("location", e.getCurrentLocation());
        evt.put("load", weight);
        evt.put("timestamp", e.getTimestamp().toString());
        eventsArr.put(evt);
      }
      requestJson.put("recentEvents", eventsArr);

      // Include placeholder delivery window
      requestJson.put("deliveryWindow", requestedDelivery.toString());
      requestJson.put("shipmentWeight", weight);

      List<Warehouse> warehouses =
          shipment.getOrder().getItems().stream()
              .flatMap(
                  item ->
                      assignmentRepository
                          .findByInventoryItem_Id(item.getInventoryItem().getId())
                          .stream())
              .map(a -> a.getStorageUnit().getWarehouse())
              .distinct()
              .collect(Collectors.toList());

      JSONArray warehouseArr = new JSONArray();
      for (Warehouse w : warehouses) warehouseArr.put(w.getName());
      requestJson.put("warehouses", warehouseArr);

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(GEMINI_API_URL))
              .header("Content-Type", "application/json")
              .header("Authorization", "Bearer " + GEMINI_API_KEY)
              .POST(HttpRequest.BodyPublishers.ofString(requestJson.toString()))
              .build();

      HttpResponse<String> response =
          HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        JSONObject respJson = new JSONObject(response.body());
        plan = new RoutePlanDto();
        plan.setShipmentId(shipment.getId());
        plan.setWarehouseSequence(
            respJson.getJSONArray("warehouseSequence").toList().stream()
                .map(Object::toString)
                .toList());
        plan.setRouteCoordinates(
            respJson.getJSONArray("routeCoordinates").toList().stream()
                .map(Object::toString)
                .toList());
        plan.setEstimatedDeliveryTime(
            LocalDateTime.parse(respJson.getString("estimatedDeliveryTime")));
        plan.setEstimatedCost(respJson.getDouble("estimatedCost"));
      } else {
        throw new IOException("Gemini API error: " + response.body());
      }

    } catch (Exception e) {
      plan =
          buildRoutePlan(
              shipment,
              event.getCurrentLocation(),
              shipment.getAddress(),
              weight,
              requestedDelivery);
    }

    shipment.setLastRoutePlan(plan);
    shipmentRepository.save(shipment);
    return plan;
  }

  private static double generateRandomWeight() {
    return 1 + (49 * random.nextDouble());
  }

  private RoutePlanDto buildRoutePlan(
      Shipment shipment,
      String startLocation,
      String endLocation,
      double weight,
      LocalDateTime requestedDelivery) {

    try {
      JSONObject requestJson = new JSONObject();
      requestJson.put("shipmentId", shipment.getId());
      requestJson.put("startLocation", startLocation);
      requestJson.put("endLocation", endLocation);
      requestJson.put("shipmentWeight", weight);
      requestJson.put("requestedDeliveryTime", requestedDelivery.toString());

      List<Warehouse> warehouses =
          shipment.getOrder().getItems().stream()
              .flatMap(
                  item ->
                      assignmentRepository
                          .findByInventoryItem_Id(item.getInventoryItem().getId())
                          .stream())
              .map(a -> a.getStorageUnit().getWarehouse())
              .distinct()
              .collect(Collectors.toList());

      JSONArray warehouseArr = new JSONArray();
      for (Warehouse w : warehouses) warehouseArr.put(w.getName());
      requestJson.put("warehouses", warehouseArr);

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(GEMINI_API_URL))
              .header("Content-Type", "application/json")
              .header("Authorization", "Bearer " + GEMINI_API_KEY)
              .POST(HttpRequest.BodyPublishers.ofString(requestJson.toString()))
              .build();

      HttpResponse<String> response =
          HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        JSONObject respJson = new JSONObject(response.body());
        RoutePlanDto plan = new RoutePlanDto();
        plan.setShipmentId(shipment.getId());
        plan.setWarehouseSequence(
            respJson.getJSONArray("warehouseSequence").toList().stream()
                .map(Object::toString)
                .toList());
        plan.setRouteCoordinates(
            respJson.getJSONArray("routeCoordinates").toList().stream()
                .map(Object::toString)
                .toList());
        plan.setEstimatedDeliveryTime(
            LocalDateTime.parse(respJson.getString("estimatedDeliveryTime")));
        plan.setEstimatedCost(respJson.getDouble("estimatedCost"));
        return plan;
      } else {
        throw new IOException("Gemini API error: " + response.body());
      }

    } catch (Exception e) {
      List<Warehouse> warehouses =
          shipment.getOrder().getItems().stream()
              .flatMap(
                  item ->
                      assignmentRepository
                          .findByInventoryItem_Id(item.getInventoryItem().getId())
                          .stream())
              .map(a -> a.getStorageUnit().getWarehouse())
              .distinct()
              .collect(Collectors.toList());

      List<String> warehouseSequence = new ArrayList<>();
      warehouseSequence.add(startLocation);
      for (Warehouse w : warehouses) {
        warehouseSequence.add(w.getName());
      }
      warehouseSequence.add(endLocation);

      List<String> coordinates = new ArrayList<>();
      for (int i = 0; i < warehouseSequence.size(); i++) {
        int x = 10 * i + (startLocation.equals("Origin") ? 0 : 5);
        int y = 5 * i + (startLocation.equals("Origin") ? 0 : 3);
        coordinates.add("POINT(" + x + " " + y + ")");
      }

      Duration estimatedDuration = Duration.ofHours(warehouses.size() * 2 + 1);
      LocalDateTime estimatedDeliveryTime = requestedDelivery.plus(estimatedDuration);
      double estimatedCost = weight * 10 + warehouses.size() * 5;

      RoutePlanDto plan = new RoutePlanDto();
      plan.setShipmentId(shipment.getId());
      plan.setWarehouseSequence(warehouseSequence);
      plan.setRouteCoordinates(coordinates);
      plan.setEstimatedDeliveryTime(estimatedDeliveryTime);
      plan.setEstimatedCost(estimatedCost);

      return plan;
    }
  }
}
