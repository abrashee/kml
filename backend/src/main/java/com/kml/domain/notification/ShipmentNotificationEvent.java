package com.kml.domain.notification;

public class ShipmentNotificationEvent {
  private final Long shipmentId;
  private final Long warehouseId;

  public ShipmentNotificationEvent(Long shipmentId, Long warehouseId) {
    this.shipmentId = shipmentId;
    this.warehouseId = warehouseId;
  }

  public Long getShipmentId() {
    return shipmentId;
  }

  public Long getWarehouseId() {
    return warehouseId;
  }
}
