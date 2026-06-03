package com.kml.shipment.entity;

import java.time.LocalDateTime;

import com.kml.common.entity.AuditableEntity;

public class ShipmentSimulationEvent extends AuditableEntity {

  private Long shipmentId;
  private String currentLocation;
  private double currentLoad;
  private LocalDateTime timestamp;

  protected ShipmentSimulationEvent() {}

  private ShipmentSimulationEvent(
      Long shipmentId, String currentLocation, double currentLoad, LocalDateTime timestamp) {

    validateShipmentId(shipmentId);
    validateCurrentLocation(currentLocation);
    validateCurrentLoad(currentLoad);
    validateTimestamp(timestamp);

    this.shipmentId = shipmentId;
    this.currentLocation = currentLocation;
    this.currentLoad = currentLoad;
    this.timestamp = timestamp;
  }

  public static ShipmentSimulationEvent create(
      Long shipmentId, String currentLocation, double currentLoad, LocalDateTime timestamp) {

    return new ShipmentSimulationEvent(shipmentId, currentLocation, currentLoad, timestamp);
  }

  private void validateShipmentId(Long shipmentId) {
    if (shipmentId == null) throw new IllegalArgumentException("ShipmentId is required");
  }

  private void validateCurrentLocation(String location) {
    if (location == null || location.isBlank())
      throw new IllegalArgumentException("Current location required");
  }

  private void validateCurrentLoad(double load) {
    if (load < 0) throw new IllegalArgumentException("Current load cannot be negative");
  }

  private void validateTimestamp(LocalDateTime timestamp) {
    if (timestamp == null) throw new IllegalArgumentException("Timestamp required");
  }

  public Long getShipmentId() {
    return shipmentId;
  }

  public String getCurrentLocation() {
    return currentLocation;
  }

  public double getCurrentLoad() {
    return currentLoad;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }
}
