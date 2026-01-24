package com.kml.capacity.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderResponseDto {
  private Long id;
  private String code;
  private String statusName;
  private List<OrderItemResponseDto> items = new ArrayList<>();
  //   private Long userId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public OrderResponseDto(
      Long id,
      String code,
      String statusName,
      List<OrderItemResponseDto> items,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.code = code;
    this.statusName = statusName;
    this.items = items;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getStatusName() {
    return statusName;
  }

  public void setStatusName(String statusName) {
    this.statusName = statusName;
  }

  public List<OrderItemResponseDto> getItems() {
    return items;
  }

  public void setItems(List<OrderItemResponseDto> items) {
    this.items = items;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public OrderResponseDto() {}
}
