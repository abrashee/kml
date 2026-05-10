package com.kml.capacity.service;

import java.util.List;

import com.kml.capacity.dto.storageUnit.StorageUnitInventoryAssignmentDto;

public interface InventoryItemLayoutService {
  List<StorageUnitInventoryAssignmentDto> getWarehouseLayout(Long warehouseId);

  List<StorageUnitInventoryAssignmentDto> getStorageUnitLayout(Long storageUnitId);
}
