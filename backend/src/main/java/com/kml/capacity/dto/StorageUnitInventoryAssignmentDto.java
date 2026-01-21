package com.kml.capacity.dto;

public class StorageUnitInventoryAssignmentDto {
  private Long storageUnitId;
  private String storageUnitCode;
  private Long inventoryItemId;
  private String inventoryItemSku;
  private String inventoryItemName;
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
