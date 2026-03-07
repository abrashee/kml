package com.kml.domain.warehouse;

import com.kml.domain.common.AuditableEntity;
import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "storage_unit_inventory_item_assignments",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"storage_unit_id", "inventory_item_id"})})
public class StorageUnitInventoryAssignment extends AuditableEntity {

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

  protected StorageUnitInventoryAssignment() {}

  private StorageUnitInventoryAssignment(
      User owner, StorageUnit storageUnit, InventoryItem inventoryItem, int assignedQuantity) {

    if (owner == null) throw new IllegalArgumentException("Owner is required");
    setOwner(owner);

    validateStorageUnit(storageUnit);
    validateInventoryItem(inventoryItem);
    validateQuantity(assignedQuantity);
    validateWithinCapacity(storageUnit, assignedQuantity);

    this.storageUnit = storageUnit;
    this.inventoryItem = inventoryItem;
    this.assignedQuantity = assignedQuantity;
  }

  /** Factory method for creating a new assignment with owner. */
  public static StorageUnitInventoryAssignment create(
      User owner, StorageUnit storageUnit, InventoryItem inventoryItem, int assignedQuantity) {
    return new StorageUnitInventoryAssignment(owner, storageUnit, inventoryItem, assignedQuantity);
  }

  public void updateAssignedQuantity(int newQuantity) {
    validateQuantity(newQuantity);
    validateWithinCapacity(this.storageUnit, newQuantity);
    this.assignedQuantity = newQuantity;
  }

  private void validateStorageUnit(StorageUnit storageUnit) {
    if (storageUnit == null) {
      throw new IllegalArgumentException("StorageUnit is required");
    }
  }

  private void validateInventoryItem(InventoryItem inventoryItem) {
    if (inventoryItem == null) {
      throw new IllegalArgumentException("InventoryItem is required");
    }
  }

  private void validateQuantity(int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be >= 1");
    }
  }

  private void validateWithinCapacity(StorageUnit storageUnit, int quantity) {
    if (storageUnit != null && quantity > storageUnit.getCapacity()) {
      throw new IllegalArgumentException("Quantity exceeds storage unit capacity");
    }
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
}
