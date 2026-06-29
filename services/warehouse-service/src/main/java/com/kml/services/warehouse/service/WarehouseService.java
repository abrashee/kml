package com.kml.services.warehouse.service;

import com.kml.services.common.security.jwt.JwtAuthenticatedUser;
import com.kml.services.warehouse.dto.StorageUnitResponseDto;
import com.kml.services.warehouse.dto.StorageUnitRequestDto;
import com.kml.services.warehouse.dto.WarehouseRequestDto;
import com.kml.services.warehouse.dto.WarehouseResponseDto;
import java.util.List;

public interface WarehouseService {

    WarehouseResponseDto createWarehouse(WarehouseRequestDto request, JwtAuthenticatedUser principal);

    WarehouseResponseDto getWarehouse(Long id, JwtAuthenticatedUser principal);

    List<WarehouseResponseDto> getWarehouses(Long ownerUserId, JwtAuthenticatedUser principal);

    List<StorageUnitResponseDto> getStorageUnits(Long warehouseId, JwtAuthenticatedUser principal);

    StorageUnitResponseDto addStorageUnit(Long warehouseId, StorageUnitRequestDto request, JwtAuthenticatedUser principal);
}
