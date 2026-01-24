package com.kml.capacity.dto;

import java.math.BigDecimal;

public class OrderItemRequestDto {
  private Long inventoryItemId;
  private int quantity;
  private BigDecimal priceAtOrder;

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

  public OrderItemRequestDto() {}

  public BigDecimal getPriceAtOrder() {
    return priceAtOrder;
  }

  public void setPriceAtOrder(BigDecimal priceAtOrder) {
    this.priceAtOrder = priceAtOrder;
  }
}
