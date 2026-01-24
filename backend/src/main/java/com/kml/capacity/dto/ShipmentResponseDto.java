package com.kml.capacity.dto;

import com.kml.domain.shipment.ShipmentStatus;
import java.time.LocalDateTime;

public class ShipmentResponseDto {
  private Long id;
  private String tracking;
  private String carrierInfo;
  private String address;
  private ShipmentStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long orderId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTracking() {
    return tracking;
  }

  public void setTracking(String tracking) {
    this.tracking = tracking;
  }

  public String getCarrierInfo() {
    return carrierInfo;
  }

  public void setCarrierInfo(String carrierInfo) {
    this.carrierInfo = carrierInfo;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public ShipmentStatus getStatus() {
    return status;
  }

  public void setStatus(ShipmentStatus status) {
    this.status = status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }
}
