package com.kml.domain.inventory;

import com.kml.domain.common.AuditableEntity;
import com.kml.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_items")
public class InventoryItem extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "sku", nullable = false, unique = true)
  private String sku;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "quantity", nullable = false)
  private int quantity;

  protected InventoryItem() {}

  public InventoryItem(User owner, String sku, String name, int quantity) {
    setOwner(owner);
    validateSku(sku);
    validateName(name);
    validateQuantity(quantity);

    this.sku = sku;
    this.name = name;
    this.quantity = quantity;
  }

  public static InventoryItem create(String sku, String name, int quantity, User createdBy) {
    InventoryItem item = new InventoryItem(createdBy, sku, name, quantity);
    return item;
  }

  public void increaseQuantity(int amount) {
    if (amount <= 0) throw new IllegalArgumentException("Inventory increase must be positive");
    this.quantity += amount;
  }

  public void decreaseQuantity(int amount) {
    if (amount <= 0) throw new IllegalArgumentException("Inventory decrease must be positive");
    if (this.quantity - amount < 0) throw new IllegalStateException("Quantity cannot be negative");
    this.quantity -= amount;
  }

  public void setQuantity(int newQuantity) {
    validateQuantity(newQuantity);
    this.quantity = newQuantity;
  }

  private void validateSku(String sku) {
    if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU is required");
  }

  private void validateName(String name) {
    if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
  }

  private void validateQuantity(int quantity) {
    if (quantity < 0) throw new IllegalArgumentException("Quantity must be >= 0");
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
}
