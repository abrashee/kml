package com.kml.capacity.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class OrderRequestDto {

  @NotBlank(message = "Order code is required")
  private String code;

  @NotNull(message = "Status ID is required")
  private Long statusId;

  @NotEmpty(message = "Order must contain at least one item")
  private List<OrderItemRequestDto> items = new ArrayList<>();

  public OrderRequestDto() {}

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Long getStatusId() {
    return statusId;
  }

  public void setStatusId(Long statusId) {
    this.statusId = statusId;
  }

  public List<OrderItemRequestDto> getItems() {
    return items;
  }

  public void setItems(List<OrderItemRequestDto> items) {
    this.items = items;
  }
}
