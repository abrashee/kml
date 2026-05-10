package com.kml.capacity.dto.ai;

import java.time.LocalDateTime;
import java.util.List;

public class RoutePlanDto {
  private Long shipmentId;
  private List<String> warehouseSequence;
  private List<String> routeCoordinates;
  private LocalDateTime estimatedDeliveryTime;
  private double estimatedCost;
  private RoutePlanDto lastRoutePlan;

  public Long getShipmentId() {
    return shipmentId;
  }

  public void setShipmentId(Long shipmentId) {
    this.shipmentId = shipmentId;
  }

  public List<String> getWarehouseSequence() {
    return warehouseSequence;
  }

  public void setWarehouseSequence(List<String> warehouseSequence) {
    this.warehouseSequence = warehouseSequence;
  }

  public List<String> getRouteCoordinates() {
    return routeCoordinates;
  }

  public void setRouteCoordinates(List<String> routeCoordinates) {
    this.routeCoordinates = routeCoordinates;
  }

  public LocalDateTime getEstimatedDeliveryTime() {
    return estimatedDeliveryTime;
  }

  public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) {
    this.estimatedDeliveryTime = estimatedDeliveryTime;
  }

  public double getEstimatedCost() {
    return estimatedCost;
  }

  public void setEstimatedCost(double estimatedCost) {
    this.estimatedCost = estimatedCost;
  }

  public RoutePlanDto getLastRoutePlan() {
    return lastRoutePlan;
  }

  public void setLastRoutePlan(RoutePlanDto lastRoutePlan) {
    this.lastRoutePlan = lastRoutePlan;
  }
}
