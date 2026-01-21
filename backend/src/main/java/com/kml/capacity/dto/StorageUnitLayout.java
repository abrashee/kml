package com.kml.capacity.dto;

import java.util.List;

public class StorageUnitLayout {
  private Long id;
  private String code;
  private Long warehouseId;
  private int capacity;
  private List<InventoryItemLayoutDto> inventoryItems;

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

  public List<InventoryItemLayoutDto> getInventoryItems() {
    return inventoryItems;
  }

  public void setInventoryItems(List<InventoryItemLayoutDto> inventoryItems) {
    this.inventoryItems = inventoryItems;
  }
}
