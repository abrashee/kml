package com.kml.capacity.dto.warehouse;

import jakarta.validation.constraints.NotBlank;

public class WarehouseRequestDto {

  @NotBlank private String name;

  @NotBlank private String address;

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
