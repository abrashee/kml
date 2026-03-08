package com.kml.capacity.dto.ai;

import java.time.LocalDateTime;

public class RoutePredictionInputDto {
  private Long shipmentId;
  private String origin;
  private String destination;
  private double shipmentWeight;
  private LocalDateTime requestedDeliveryTime;

  public Long getShipmentId() {
    return shipmentId;
  }

  public void setShipmentId(Long shipmentId) {
    this.shipmentId = shipmentId;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public double getShipmentWeight() {
    return shipmentWeight;
  }

  public void setShipmentWeight(double shipmentWeight) {
    this.shipmentWeight = shipmentWeight;
  }

  public LocalDateTime getRequestedDeliveryTime() {
    return requestedDeliveryTime;
  }

  public void setRequestedDeliveryTime(LocalDateTime requestedDeliveryTime) {
    this.requestedDeliveryTime = requestedDeliveryTime;
  }
}
