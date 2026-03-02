package com.kml.capacity.dto;

import java.time.LocalDateTime;

import com.kml.domain.shipment.ShipmentStatus;

public class ShipmentHistoryResponseDto {
  private Long id;
  private ShipmentStatus previousStatus;
  private ShipmentStatus newStatus;
  private LocalDateTime changedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ShipmentStatus getPreviousStatus() {
    return previousStatus;
  }

  public void setPreviousStatus(ShipmentStatus previousStatus) {
    this.previousStatus = previousStatus;
  }

  public ShipmentStatus getNewStatus() {
    return newStatus;
  }

  public void setNewStatus(ShipmentStatus newStatus) {
    this.newStatus = newStatus;
  }

  public LocalDateTime getChangedAt() {
    return changedAt;
  }

  public void setChangedAt(LocalDateTime changedAt) {
    this.changedAt = changedAt;
  }
}
