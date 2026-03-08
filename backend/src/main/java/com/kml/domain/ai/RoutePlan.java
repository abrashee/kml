package com.kml.domain.ai;

import com.kml.domain.common.AuditableEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoutePlan extends AuditableEntity {

  private Long shipmentId;
  private List<Long> warehouseSequence = new ArrayList<>();
  private List<String> routeCoordinates = new ArrayList<>();
  private LocalDateTime estimatedDeliveryTime;
  private double estimatedCost;

  protected RoutePlan() {}

  private RoutePlan(
      Long shipmentId,
      List<Long> warehouseSequence,
      List<String> routeCoordinates,
      LocalDateTime estimatedDeliveryTime,
      double estimatedCost) {

    validateShipmentId(shipmentId);
    validateWarehouseSequence(warehouseSequence);
    validateRouteCoordinates(routeCoordinates);
    validateEstimatedDeliveryTime(estimatedDeliveryTime);
    validateEstimatedCost(estimatedCost);

    this.shipmentId = shipmentId;
    this.warehouseSequence = new ArrayList<>(warehouseSequence);
    this.routeCoordinates = new ArrayList<>(routeCoordinates);
    this.estimatedDeliveryTime = estimatedDeliveryTime;
    this.estimatedCost = estimatedCost;
  }

  public static RoutePlan create(
      Long shipmentId,
      List<Long> warehouseSequence,
      List<String> routeCoordinates,
      LocalDateTime estimatedDeliveryTime,
      double estimatedCost) {

    return new RoutePlan(
        shipmentId, warehouseSequence, routeCoordinates, estimatedDeliveryTime, estimatedCost);
  }

  private void validateShipmentId(Long shipmentId) {
    if (shipmentId == null) throw new IllegalArgumentException("ShipmentId is required");
  }

  private void validateWarehouseSequence(List<Long> sequence) {
    if (sequence == null || sequence.isEmpty())
      throw new IllegalArgumentException("Warehouse sequence must not be empty");
  }

  private void validateRouteCoordinates(List<String> coordinates) {
    if (coordinates == null) throw new IllegalArgumentException("Route coordinates required");
  }

  private void validateEstimatedDeliveryTime(LocalDateTime time) {
    if (time == null) throw new IllegalArgumentException("Estimated delivery time required");
  }

  private void validateEstimatedCost(double cost) {
    if (cost < 0) throw new IllegalArgumentException("Estimated cost cannot be negative");
  }

  public Long getShipmentId() {
    return shipmentId;
  }

  public List<Long> getWarehouseSequence() {
    return Collections.unmodifiableList(warehouseSequence);
  }

  public List<String> getRouteCoordinates() {
    return Collections.unmodifiableList(routeCoordinates);
  }

  public LocalDateTime getEstimatedDeliveryTime() {
    return estimatedDeliveryTime;
  }

  public double getEstimatedCost() {
    return estimatedCost;
  }

  public void updateEstimatedDeliveryTime(LocalDateTime newTime) {
    validateEstimatedDeliveryTime(newTime);
    this.estimatedDeliveryTime = newTime;
  }

  public void updateEstimatedCost(double newCost) {
    validateEstimatedCost(newCost);
    this.estimatedCost = newCost;
  }
}
