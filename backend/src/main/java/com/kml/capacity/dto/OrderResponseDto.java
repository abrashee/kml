package com.kml.capacity.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderResponseDto {
  private Long id;
  private String code;
  private Long statusId;
  private String statusName;
  private Long userId;
  private String username;

  private List<OrderItemResponseDto> items = new ArrayList<>();
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public OrderResponseDto() {}

  public OrderResponseDto(
      Long id,
      String code,
      Long statusId,
      String statusName,
      Long userId,
      String username,
      List<OrderItemResponseDto> items,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.code = code;
    this.statusId = statusId;
    this.statusName = statusName;
    this.userId = userId;
    this.username = username;
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

  public Long getStatusId() {
    return statusId;
  }

  public void setStatusId(Long statusId) {
    this.statusId = statusId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
