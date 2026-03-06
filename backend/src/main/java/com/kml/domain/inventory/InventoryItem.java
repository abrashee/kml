package com.kml.domain.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; // Changed from LocalDateTime for UTC consistency
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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

  protected InventoryItem() {}

  public InventoryItem(String sku, String name, int quantity) {
    validateSku(sku);
    validateName(name);
    validateQuantity(quantity);

    this.sku = sku;
    this.name = name;
    this.quantity = quantity;
  }

  public void increaseQuantity(int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Inventory increase amount must be positive");
    }
    this.quantity += amount;
  }

  public void decreaseQuantity(int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Inventory decrease amount must be positive");
    }
    if (this.quantity - amount < 0) {
      throw new IllegalStateException("Inventory quantity cannot be negative");
    }
    this.quantity -= amount;
  }

  public void setQuantity(int newQuantity) {
    validateQuantity(newQuantity);
    this.quantity = newQuantity;
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

  private void validateSku(String sku) {
    if (sku == null || sku.isBlank()) {
      throw new IllegalArgumentException("Inventory SKU cannot be null or empty");
    }
  }

  private void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Inventory name cannot be null or empty");
    }
  }

  private void validateQuantity(int quantity) {
    if (quantity < 0) {
      throw new IllegalArgumentException("Inventory quantity must be zero or greater");
    }
  }

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
