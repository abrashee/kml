package com.kml.domain.ai;

import com.kml.domain.common.AuditableEntity;
import java.time.LocalDateTime;

public class RoutePredictionInput extends AuditableEntity {

  private Long shipmentId;
  private String origin;
  private String destination;
  private double shipmentWeight;
  private LocalDateTime requestedDeliveryTime;

  protected RoutePredictionInput() {}

  private RoutePredictionInput(
      Long shipmentId,
      String origin,
      String destination,
      double shipmentWeight,
      LocalDateTime requestedDeliveryTime) {

    validateShipmentId(shipmentId);
    validateOrigin(origin);
    validateDestination(destination);
    validateShipmentWeight(shipmentWeight);
    validateRequestedDeliveryTime(requestedDeliveryTime);

    this.shipmentId = shipmentId;
    this.origin = origin;
    this.destination = destination;
    this.shipmentWeight = shipmentWeight;
    this.requestedDeliveryTime = requestedDeliveryTime;
  }

  public static RoutePredictionInput create(
      Long shipmentId,
      String origin,
      String destination,
      double shipmentWeight,
      LocalDateTime requestedDeliveryTime) {

    return new RoutePredictionInput(
        shipmentId, origin, destination, shipmentWeight, requestedDeliveryTime);
  }

  private void validateShipmentId(Long shipmentId) {
    if (shipmentId == null) throw new IllegalArgumentException("ShipmentId is required");
  }

  private void validateOrigin(String origin) {
    if (origin == null || origin.isBlank())
      throw new IllegalArgumentException("Origin is required");
  }

  private void validateDestination(String destination) {
    if (destination == null || destination.isBlank())
      throw new IllegalArgumentException("Destination is required");
  }

  private void validateShipmentWeight(double weight) {
    if (weight <= 0) throw new IllegalArgumentException("Shipment weight must be positive");
  }

  private void validateRequestedDeliveryTime(LocalDateTime time) {
    if (time == null) throw new IllegalArgumentException("Requested delivery time required");
  }

  public Long getShipmentId() {
    return shipmentId;
  }

  public String getOrigin() {
    return origin;
  }

  public String getDestination() {
    return destination;
  }

  public double getShipmentWeight() {
    return shipmentWeight;
  }

  public LocalDateTime getRequestedDeliveryTime() {
    return requestedDeliveryTime;
  }
}
