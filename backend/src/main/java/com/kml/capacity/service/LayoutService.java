package com.kml.capacity.service;

import java.util.List;

import com.kml.domain.warehouse.StorageUnitInventoryAssignment;

public interface LayoutService {
  List<StorageUnitInventoryAssignment> getWarehouseLayout(Long warehouseId);

  List<StorageUnitInventoryAssignment> getStorageUnitLayout(Long storageUnitId);
}
