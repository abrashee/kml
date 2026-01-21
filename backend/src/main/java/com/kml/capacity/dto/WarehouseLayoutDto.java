package com.kml.capacity.dto;

import java.util.List;

public class WarehouseLayoutDto {
  private Long id;
  private String name;
  private String address;
  private List<StorageUnitLayout> storageUnits;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public List<StorageUnitLayout> getStorageUnits() {
    return storageUnits;
  }

  public void setStorageUnits(List<StorageUnitLayout> storageUnits) {
    this.storageUnits = storageUnits;
  }
}
