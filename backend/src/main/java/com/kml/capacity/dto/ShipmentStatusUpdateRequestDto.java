package com.kml.capacity.dto;

import com.kml.domain.shipment.ShipmentStatus;
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
