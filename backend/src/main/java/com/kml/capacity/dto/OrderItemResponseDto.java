package com.kml.capacity.dto;

import java.math.BigDecimal;

public class OrderItemResponseDto {
  private Long id;
  private Long inventoryItemId;
  private int quantity;
  private BigDecimal priceAtOrder;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getInventoryItemId() {
    return inventoryItemId;
  }

  public void setInventoryItemId(Long inventoryItemId) {
    this.inventoryItemId = inventoryItemId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getPriceAtOrder() {
    return priceAtOrder;
  }

  public void setPriceAtOrder(BigDecimal priceAtOrder) {
    this.priceAtOrder = priceAtOrder;
  }

  public OrderItemResponseDto() {}
}
