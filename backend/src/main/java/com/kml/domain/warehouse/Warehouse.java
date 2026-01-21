package com.kml.domain.warehouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "warehouses")
public class Warehouse {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "address", nullable = false)
  private String address;

  @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<StorageUnit> storageUnits = new ArrayList<>();

  protected Warehouse() {}

  public Warehouse(String name, String address) {
    validateName(name);
    validateAddress(address);

    this.name = name;
    this.address = address;
  }

  private void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Warehouse name must not be null");
    }
  }

  private void validateAddress(String address) {
    if (address == null || address.isBlank()) {
      throw new IllegalArgumentException("Warehouse address must not be null");
    }
  }

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

  public void addStorageUnit(StorageUnit unit) {
    if (unit == null) {
      throw new IllegalArgumentException("unit must not be null");
    }
    unit.assignToWarehouse(this);
    storageUnits.add(unit);
  }

  public void removeStorageUnit(StorageUnit unit) {
    if (unit == null) return;
    unit.unassignWarehouse();
    storageUnits.remove(unit);
  }

  public List<StorageUnit> getStorageUnits() {
    return Collections.unmodifiableList(storageUnits);
  }
}
