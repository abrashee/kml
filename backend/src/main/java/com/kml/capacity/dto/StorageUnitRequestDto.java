package com.kml.capacity.dto;

import jakarta.validation.constraints.NotBlank;

public class StorageUnitRequestDto {

  @NotBlank private String code;
  private Long warehouseId;
  private int capacity;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public Long getWarehouseId() {
    return warehouseId;
  }

  public void setWarehouseId(Long warehouseId) {
    this.warehouseId = warehouseId;
  }
}
