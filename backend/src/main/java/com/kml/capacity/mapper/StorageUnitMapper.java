package com.kml.capacity.mapper;

import java.util.List;

import com.kml.capacity.dto.InventoryItemLayoutDto;
import com.kml.capacity.dto.StorageUnitInventoryAssignmentDto;
import com.kml.capacity.dto.StorageUnitLayoutDto;
import com.kml.capacity.dto.StorageUnitResponseDto;
import com.kml.capacity.dto.WarehouseLayoutDto;
import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.warehouse.StorageUnit;
import com.kml.domain.warehouse.StorageUnitInventoryAssignment;
import com.kml.domain.warehouse.Warehouse;

public final class StorageUnitMapper {

  private StorageUnitMapper() {}

  public static StorageUnitResponseDto toDto(StorageUnit entity) {
    if (entity == null) return null;

    return new StorageUnitResponseDto(
        entity.getId(),
        entity.getCode(),
        entity.getWarehouse() != null ? entity.getWarehouse().getId() : null,
        entity.getCapacity());
  }

  public static StorageUnitInventoryAssignmentDto toDto(StorageUnitInventoryAssignment entity) {
    if (entity == null) return null;

    return new StorageUnitInventoryAssignmentDto(
        entity.getStorageUnit().getId(),
        entity.getStorageUnit().getCode(),
        entity.getInventoryItem().getId(),
        entity.getInventoryItem().getSku(),
        entity.getInventoryItem().getName(),
        entity.getAssignedQuantity());
  }

  public static StorageUnitLayoutDto toLayoutDto(
      StorageUnit unit, List<InventoryItemLayoutDto> inventoryItems) {
    if (unit == null) return null;

    return new StorageUnitLayoutDto(
        unit.getId(),
        unit.getCode(),
        unit.getWarehouse() != null ? unit.getWarehouse().getId() : null,
        unit.getCapacity(),
        inventoryItems != null ? inventoryItems : List.of());
  }

  public static InventoryItemLayoutDto toInventoryItemLayoutDto(InventoryItem item) {
    if (item == null) return null;

    return new InventoryItemLayoutDto(
        item.getId(),
        item.getSku(),
        item.getName(),
        item.getQuantity(),
        item.getCreatedAt(),
        item.getUpdatedAt());
  }

  public static WarehouseLayoutDto toWarehouseLayoutDto(
      Warehouse warehouse, List<StorageUnitLayoutDto> storageUnitLayouts) {
    if (warehouse == null) return null;

    return new WarehouseLayoutDto(
        warehouse.getId(),
        warehouse.getName(),
        warehouse.getAddress(),
        storageUnitLayouts != null ? storageUnitLayouts : List.of());
  }
}
