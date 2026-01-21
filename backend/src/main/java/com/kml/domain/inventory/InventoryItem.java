package com.kml.domain.inventory;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "sku", nullable = false, unique = true)
  private String sku;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "quantity", nullable = false)
  private int quantity;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  // JPA
  protected InventoryItem() {}

  public InventoryItem(String sku, String name, int quantity) {
    validateSku(sku);
    validateName(name);
    validateQuantity(quantity);

    this.sku = sku;
    this.name = name;
    this.quantity = quantity;
  }

  // Domain
  public void increaseQuantity(int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Increase amount must be positive");
    }
    this.quantity += amount;
  }

  public void decreaseQuantity(int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Decrease amount must be positive");
    }
    if (this.quantity - amount < 0) {
      throw new IllegalStateException("Inventory quantity cannot be negative");
    }
    this.quantity -= amount;
  }

  // Persistence
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

  // Validation helpers
  private void validateSku(String sku) {
    if (sku == null || sku.isBlank()) {
      throw new IllegalArgumentException("SKU must not be null or blank");
    }
  }

  private void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name must not be null or blank");
    }
  }

  private void validateQuantity(int quantity) {
    if (quantity < 0) {
      throw new IllegalArgumentException("Quantity must be zero or greater");
    }
  }

  // Getters
  public Long getId() {
    return id;
  }

  public String getSku() {
    return sku;
  }

  public String getName() {
    return name;
  }

  public int getQuantity() {
    return quantity;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
