package com.kml.services.warehouse.service;

import com.kml.services.warehouse.dto.StorageUnitResponseDto;
import com.kml.services.warehouse.dto.StorageUnitRequestDto;
import com.kml.services.warehouse.dto.WarehouseRequestDto;
import com.kml.services.warehouse.dto.WarehouseResponseDto;
import java.util.List;

public interface WarehouseService {

    WarehouseResponseDto createWarehouse(WarehouseRequestDto request);

    WarehouseResponseDto getWarehouse(Long id);

    List<WarehouseResponseDto> getWarehouses(Long ownerUserId);

    List<StorageUnitResponseDto> getStorageUnits(Long warehouseId);

    StorageUnitResponseDto addStorageUnit(Long warehouseId, StorageUnitRequestDto request);
}
