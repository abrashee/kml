package com.kml.capacity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class OrderRequestDto {
  @NotBlank(message = "Order code is required")
  private String code;

  @NotNull(message = "Status ID is required")
  private Long statusId;

  @NotEmpty(message = "Order msut contain at least one item")
  private List<OrderItemRequestDto> items = new ArrayList<>();

  //   private Long userId;

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

  public OrderRequestDto() {}
}
