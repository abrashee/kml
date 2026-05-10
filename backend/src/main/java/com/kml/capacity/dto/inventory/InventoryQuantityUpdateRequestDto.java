package com.kml.capacity.dto.inventory;

import jakarta.validation.constraints.NotNull;

public class InventoryQuantityUpdateRequestDto {

  @NotNull(message = "Delta is required")
  private Integer delta;

  public InventoryQuantityUpdateRequestDto() {}

  public InventoryQuantityUpdateRequestDto(Integer delta) {
    this.delta = delta;
  }

  public Integer getDelta() {
    return delta;
  }

  public void setDelta(Integer delta) {
    this.delta = delta;
  }
}
