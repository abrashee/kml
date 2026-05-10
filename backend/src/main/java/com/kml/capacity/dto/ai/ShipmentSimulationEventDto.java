package com.kml.capacity.dto.ai;

import java.time.LocalDateTime;

public class ShipmentSimulationEventDto {
  private Long shipmentId;
  private String currentLocation;
  private double currentLoad;
  private LocalDateTime timestamp;

  public enum SimulationEventType {
    LOCATION_UPDATE,
    DELAY,
    LOAD_CHANGE
  }

  private SimulationEventType eventType;

  public Long getShipmentId() {
    return shipmentId;
  }

  public void setShipmentId(Long shipmentId) {
    this.shipmentId = shipmentId;
  }

  public String getCurrentLocation() {
    return currentLocation;
  }

  public void setCurrentLocation(String currentLocation) {
    this.currentLocation = currentLocation;
  }

  public double getCurrentLoad() {
    return currentLoad;
  }

  public void setCurrentLoad(double currentLoad) {
    this.currentLoad = currentLoad;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public SimulationEventType getEventType() {
    return eventType;
  }

  public void setEventType(SimulationEventType eventType) {
    this.eventType = eventType;
  }
}
