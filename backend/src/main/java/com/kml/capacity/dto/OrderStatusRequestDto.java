package com.kml.capacity.dto;

import jakarta.validation.constraints.NotBlank;

public class OrderStatusRequestDto {
  @NotBlank private String name;
  private String description;

  public OrderStatusRequestDto() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
