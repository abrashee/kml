package com.kml.capacity.dto;

import jakarta.validation.constraints.NotBlank;

public class WarehouseRequestDto {

  @NotBlank private String name;

  private String address;

  public WarehouseRequestDto() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
