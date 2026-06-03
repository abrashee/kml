package com.kml.shipment.entity;

public final class ShipmentNotificationEvent {

  private final Long ownerId;
  private final Long shipmentId;

  public ShipmentNotificationEvent(Long ownerId, Long shipmentId) {
    if (ownerId == null) throw new IllegalArgumentException("Owner ID is required");
    if (shipmentId == null) throw new IllegalArgumentException("Shipment ID is required");

    this.ownerId = ownerId;
    this.shipmentId = shipmentId;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public Long getShipmentId() {
    return shipmentId;
  }
}
