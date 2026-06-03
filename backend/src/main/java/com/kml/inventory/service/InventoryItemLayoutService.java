package com.kml.inventory.service;

import java.util.List;

import com.kml.warehouse.storageUnit.dto.StorageUnitInventoryAssignmentDto;

public interface InventoryItemLayoutService {
  List<StorageUnitInventoryAssignmentDto> getWarehouseLayout(Long warehouseId);

  List<StorageUnitInventoryAssignmentDto> getStorageUnitLayout(Long storageUnitId);
}
