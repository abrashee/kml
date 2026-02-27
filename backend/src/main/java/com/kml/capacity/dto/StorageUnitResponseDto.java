package com.kml.capacity.dto;

public class StorageUnitResponseDto {

  private Long id;
  private String code;
  private Long warehouseId;
  private int capacity;

  public StorageUnitResponseDto() {}

  public StorageUnitResponseDto(Long id, String code, Long warehouseId, int capacity) {
    this.id = id;
    this.code = code;
    this.warehouseId = warehouseId;
    this.capacity = capacity;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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
