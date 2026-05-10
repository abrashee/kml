package com.kml.domain.order;

import java.math.BigDecimal;

import com.kml.domain.common.AuditableEntity;
import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Order order;

  @ManyToOne(optional = false)
  private InventoryItem inventoryItem;

  @Column(nullable = false)
  private int quantity;

  @Column(nullable = false)
  private BigDecimal priceAtOrder;

  protected OrderItem() {}

  public OrderItem(User owner, InventoryItem inventoryItem, int quantity, BigDecimal priceAtOrder) {
    setOwner(owner);
    validate(inventoryItem, quantity, priceAtOrder);
    this.inventoryItem = inventoryItem;
    this.quantity = quantity;
    this.priceAtOrder = priceAtOrder;
  }

  public static OrderItem create(
      User owner, InventoryItem inventoryItem, int quantity, BigDecimal priceAtOrder) {
    return new OrderItem(owner, inventoryItem, quantity, priceAtOrder);
  }

  private void validate(InventoryItem item, int qty, BigDecimal price) {
    if (item == null) throw new IllegalArgumentException("Inventory item required");
    if (qty <= 0) throw new IllegalArgumentException("Quantity must be positive");
    if (price == null || price.signum() <= 0)
      throw new IllegalArgumentException("Price must be positive");
  }

  protected void setOrder(Order order) {
    this.order = order;
  }

  public Long getId() {
    return id;
  }

  public Order getOrder() {
    return order;
  }

  public InventoryItem getInventoryItem() {
    return inventoryItem;
  }

  public int getQuantity() {
    return quantity;
  }

  public BigDecimal getPriceAtOrder() {
    return priceAtOrder;
  }
}
