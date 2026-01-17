package com.kml.capacity.service;

import com.kml.domain.warehouse.StorageUnit;

public interface StorageUnitService {
  // Create storageunit with a warehouse

  StorageUnit createStorageUnit(String code, Long warehouseId, int capacity);
}
