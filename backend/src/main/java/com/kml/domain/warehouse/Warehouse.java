package com.kml.domain.warehouse;

import com.kml.domain.common.AuditableEntity;
import com.kml.domain.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "warehouses")
public class Warehouse extends AuditableEntity {

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

  private Warehouse(User owner, String name, String address) {
    if (owner == null) throw new IllegalArgumentException("Owner is required");
    setOwner(owner);
    validateName(name);
    validateAddress(address);
    this.name = name;
    this.address = address;
  }

  public static Warehouse create(User owner, String name, String address) {
    return new Warehouse(owner, name, address);
  }

  public void rename(String newName) {
    validateName(newName);
    this.name = newName;
  }

  public void changeAddress(String newAddress) {
    validateAddress(newAddress);
    this.address = newAddress;
  }

  public void addStorageUnit(StorageUnit unit) {
    if (unit == null) throw new IllegalArgumentException("StorageUnit must not be null");
    unit.assignToWarehouse(this);
    storageUnits.add(unit);
  }

  public void removeStorageUnit(StorageUnit unit) {
    if (unit == null) return;
    unit.unassignWarehouse();
    storageUnits.remove(unit);
  }

  private void validateName(String name) {
    if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
  }

  private void validateAddress(String address) {
    if (address == null || address.isBlank())
      throw new IllegalArgumentException("Address is required");
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  public List<StorageUnit> getStorageUnits() {
    return Collections.unmodifiableList(storageUnits);
  }
}
