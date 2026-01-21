package com.kml.capacity.service;

import java.util.List;
import java.util.Optional;

import com.kml.domain.warehouse.StorageUnit;

public interface StorageUnitService {

  StorageUnit createStorageUnit(String code, Long warehouseId, int capacity);

  // Get
  List<StorageUnit> getStorageUnitsByWarehouseId(Long warehouseId);

  Optional<StorageUnit> getStorageUnitById(Long id);

  Optional<StorageUnit> getStorageUnitByCode(String code);

  Optional<StorageUnit> getStorageUnitByWarehouseIdAndCode(Long warehouseId, String code);
}
