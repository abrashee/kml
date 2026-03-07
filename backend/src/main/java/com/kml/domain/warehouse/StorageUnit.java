package com.kml.domain.warehouse;

import com.kml.domain.common.AuditableEntity;
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
    name = "storage_units",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"warehouse_id", "code"})})
public class StorageUnit extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String code;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "warehouse_id", nullable = false)
  private Warehouse warehouse;

  @Column(nullable = false)
  private int capacity;

  protected StorageUnit() {}

  private StorageUnit(User owner, String code, int capacity) {
    setOwner(owner);
    validateCode(code);
    validateCapacity(capacity);
    this.code = code;
    this.capacity = capacity;
  }

  public static StorageUnit create(User owner, String code, int capacity) {
    if (owner == null) {
      throw new IllegalArgumentException("Owner must not be null");
    }
    return new StorageUnit(owner, code, capacity);
  }

  public void updateCapacity(int newCapacity) {
    validateCapacity(newCapacity);
    this.capacity = newCapacity;
  }

  public void assignToWarehouse(Warehouse warehouse) {
    if (warehouse == null) {
      throw new IllegalArgumentException("Warehouse must not be null");
    }

    if (this.warehouse != null) {
      throw new IllegalStateException("Already assigned to a warehouse");
    }

    this.warehouse = warehouse;
  }

  public void unassignWarehouse() {
    this.warehouse = null;
  }

  private void validateCode(String code) {
    if (code == null || code.isBlank()) {
      throw new IllegalArgumentException("Code is required");
    }
  }

  private void validateCapacity(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be > 0");
    }
  }

  public Long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public Warehouse getWarehouse() {
    return warehouse;
  }

  public int getCapacity() {
    return capacity;
  }
}
