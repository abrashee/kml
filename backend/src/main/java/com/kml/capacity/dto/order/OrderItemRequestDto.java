package com.kml.capacity.dto.order;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderItemRequestDto {

  @NotNull private Long inventoryItemId;

  @Min(1)
  private int quantity;

  @NotNull private BigDecimal priceAtOrder;

  public OrderItemRequestDto() {}

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
}
