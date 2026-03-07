package com.kml.capacity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StorageUnitRequestDto {

  @NotBlank(message = "Storage unit code is required")
  private String code;

  @NotNull(message = "warehouseId is required")
  private Long warehouseId;

  @Min(value = 0, message = "capacity must be zero or greater")
  private int capacity;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Long getWarehouseId() {
    return warehouseId;
  }

  public void setWarehouseId(Long warehouseId) {
    this.warehouseId = warehouseId;
  }

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }
}
