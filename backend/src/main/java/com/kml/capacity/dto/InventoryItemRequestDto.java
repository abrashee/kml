package com.kml.capacity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class InventoryItemRequestDto {

  @NotBlank(message = "SKU is required")
  private String sku;

  @NotBlank(message = "Name is required")
  private String name;

  @Min(value = 0, message = "Quantity must be zero or greater")
  private int quantity;

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}
