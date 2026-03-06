package com.kml.domain.shipment;

public final class ShipmentNotificationEvent {

  private final Long shipmentId;
  private final Long warehouseId;

  public ShipmentNotificationEvent(Long shipmentId, Long warehouseId) {
    if (shipmentId == null) throw new IllegalArgumentException("Shipment ID is required");
    if (warehouseId == null) throw new IllegalArgumentException("Warehouse ID is required");

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
