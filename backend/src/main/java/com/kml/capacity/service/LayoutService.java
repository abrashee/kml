package com.kml.capacity.service;

import java.util.List;

import com.kml.capacity.dto.StorageUnitInventoryAssignmentDto;

public interface LayoutService {
  List<StorageUnitInventoryAssignmentDto> getWarehouseLayout(Long warehouseId);

  List<StorageUnitInventoryAssignmentDto> getStorageUnitLayout(Long storageUnitId);
}
