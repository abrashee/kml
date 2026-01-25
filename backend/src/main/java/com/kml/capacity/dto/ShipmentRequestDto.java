package com.kml.capacity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ShipmentRequestDto {

  private String tracking;
  private String carrierInfo;
  @NotBlank private String address;
  @NotNull private Long orderId;

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

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }
}
