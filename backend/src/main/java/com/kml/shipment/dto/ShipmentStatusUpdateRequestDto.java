package com.kml.shipment.dto;

import com.kml.shipment.entity.ShipmentStatus;

import jakarta.validation.constraints.NotNull;

public class ShipmentStatusUpdateRequestDto {
  @NotNull private ShipmentStatus status;

  public ShipmentStatus getStatus() {
    return status;
  }

  public void setStatus(ShipmentStatus status) {
    this.status = status;
  }
}
