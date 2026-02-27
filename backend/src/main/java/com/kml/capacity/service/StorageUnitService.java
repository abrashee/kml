package com.kml.capacity.service;

import java.util.List;

import com.kml.capacity.dto.StorageUnitResponseDto;

public interface StorageUnitService {

  StorageUnitResponseDto createStorageUnit(String code, Long warehouseId, int capacity);

  List<StorageUnitResponseDto> getStorageUnitsByWarehouseId(Long warehouseId);

  StorageUnitResponseDto getStorageUnitById(Long id);

  StorageUnitResponseDto getStorageUnitByCode(String code);

  StorageUnitResponseDto getStorageUnitByWarehouseIdAndCode(Long warehouseId, String code);

  void deleteStorageUnit(Long id);
}
