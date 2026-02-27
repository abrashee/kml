package com.kml.capacity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StorageUnitInventoryAssignmentDto {

  @NotNull private Long storageUnitId;
  private String storageUnitCode;

  @NotNull private Long inventoryItemId;
  private String inventoryItemSku;
  private String inventoryItemName;

  @Min(1)
  private int assignedQuantity;

  public Long getStorageUnitId() {
    return storageUnitId;
  }

  public void setStorageUnitId(Long storageUnitId) {
    this.storageUnitId = storageUnitId;
  }

  public String getStorageUnitCode() {
    return storageUnitCode;
  }

  public void setStorageUnitCode(String storageUnitCode) {
    this.storageUnitCode = storageUnitCode;
  }

  public Long getInventoryItemId() {
    return inventoryItemId;
  }

  public void setInventoryItemId(Long inventoryItemId) {
    this.inventoryItemId = inventoryItemId;
  }

  public String getInventoryItemSku() {
    return inventoryItemSku;
  }

  public void setInventoryItemSku(String inventoryItemSku) {
    this.inventoryItemSku = inventoryItemSku;
  }

  public String getInventoryItemName() {
    return inventoryItemName;
  }

  public void setInventoryItemName(String inventoryItemName) {
    this.inventoryItemName = inventoryItemName;
  }

  public int getAssignedQuantity() {
    return assignedQuantity;
  }

  public void setAssignedQuantity(int assignedQuantity) {
    this.assignedQuantity = assignedQuantity;
  }
}
