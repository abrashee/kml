package com.kml.domain.order;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_item_id", nullable = false)
  private InventoryItem inventoryItem;

  @Column(name = "quantity", nullable = false)
  private int quantity;

  @Column(name = "price_at_order", nullable = false)
  private BigDecimal priceAtOrder;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  protected OrderItem() {}

  public OrderItem(InventoryItem inventoryItem, int quantity, BigDecimal priceAtOrder) {
    this.inventoryItem = inventoryItem;
    this.quantity = quantity;
    this.priceAtOrder = priceAtOrder;

    validate();
  }

  private void validate() {
    if (inventoryItem == null) throw new IllegalArgumentException("InventoryItem is required");
    if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
    if (priceAtOrder == null || priceAtOrder.signum() <= 0)
      throw new IllegalArgumentException("Price must be Posiitive");
  }

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

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public InventoryItem getInventoryItem() {
    return inventoryItem;
  }

  public void setInventoryItem(InventoryItem inventoryItem) {
    this.inventoryItem = inventoryItem;
  }

  public int getQuantity() {
    return quantity;
  }

  public BigDecimal getPriceAtOrder() {
    return priceAtOrder;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
