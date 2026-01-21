package com.kml.domain.warehouse;

import java.time.LocalDateTime;

import com.kml.domain.inventory.InventoryItem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "storage_unit_inventory_item_assignments",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"storage_unit_id", "inventory_item_id"})})
public class StorageUnitInventoryAssignment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "storage_unit_id", nullable = false)
  private StorageUnit storageUnit;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "inventory_item_id", nullable = false)
  private InventoryItem inventoryItem;

  @Column(name = "assigned_quantity", nullable = false)
  private int assignedQuantity;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  protected StorageUnitInventoryAssignment() {}

  public StorageUnitInventoryAssignment(
      StorageUnit storageUnit, InventoryItem inventoryItem, int assignedQuantity) {

    validateStorageUnit(storageUnit);
    validateInventoryItem(inventoryItem);

    this.storageUnit = storageUnit;
    this.inventoryItem = inventoryItem;

    validateAssignedQuantity(assignedQuantity);
    this.assignedQuantity = assignedQuantity;
  }

  public void updateAssignedQuantity(int newQuantity) {
    validateAssignedQuantity(newQuantity);
    this.assignedQuantity = newQuantity;
  }

  // validators
  private void validateStorageUnit(StorageUnit storageUnit) {
    if (storageUnit == null) {
      throw new IllegalArgumentException("StorageUnit must not be null");
    }
  }

  private void validateInventoryItem(InventoryItem inventoryItem) {
    if (inventoryItem == null) {
      throw new IllegalArgumentException("Inventory Item must not be null");
    }
  }

  private void validateAssignedQuantity(int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Assigned quantity must be at least 1");
    }
    if (inventoryItem != null && quantity > inventoryItem.getQuantity()) {
      throw new IllegalArgumentException("Assigned quantity exceeds available inventory");
    }
    if (storageUnit != null && quantity > storageUnit.getCapacity()) {
      throw new IllegalArgumentException("Assigned quanitty exceeds storageUnit capacity");
    }
  }

  // Persistence
  @PrePersist
  public void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public StorageUnit getStorageUnit() {
    return storageUnit;
  }

  public InventoryItem getInventoryItem() {
    return inventoryItem;
  }

  public int getAssignedQuantity() {
    return assignedQuantity;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
