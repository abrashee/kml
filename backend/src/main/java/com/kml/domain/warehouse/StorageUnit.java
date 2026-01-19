package com.kml.domain.warehouse;

import java.time.LocalDateTime;

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

@Entity
@Table(name = "storage_units")
public class StorageUnit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String code;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "warehouse_id", nullable = false)
  private Warehouse warehouse;

  @Column(nullable = false)
  private int capacity;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  protected StorageUnit() {}

  public StorageUnit(String code, int capacity) {
    validateCode(code);
    validateCapacity(capacity);
    this.code = code;
    this.capacity = capacity;
  }

  public void updateCapacity(int newCapacity) {
    validateCapacity(newCapacity);
    this.capacity = newCapacity;
  }

  @PrePersist
  protected void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  private void validateCode(String code) {
    if (code == null || code.isBlank()) {
      throw new IllegalArgumentException("Storage unit code must not be blank");
    }
  }

  private void validateCapacity(int capacity) {
    if (capacity < 0) {
      throw new IllegalArgumentException("Capacity must be zero or greater");
    }
  }

  public void assignToWarehouse(Warehouse warehouse) {
    if (this.warehouse != null) {
      throw new IllegalArgumentException("StorageUnit already assigned to a warehosue");
    }
    this.warehouse = warehouse;
  }

  public void unassignWarehouse() {
    this.warehouse = null;
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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
