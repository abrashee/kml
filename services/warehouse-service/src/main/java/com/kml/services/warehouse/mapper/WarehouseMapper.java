package com.kml.services.warehouse.mapper;

import com.kml.services.warehouse.dto.StorageUnitResponseDto;
import com.kml.services.warehouse.dto.WarehouseResponseDto;
import com.kml.services.warehouse.entity.StorageUnit;
import com.kml.services.warehouse.entity.Warehouse;

public final class WarehouseMapper {

    private WarehouseMapper() {
    }

    public static WarehouseResponseDto toDto(Warehouse warehouse) {
        return new WarehouseResponseDto(
            warehouse.getId(),
            warehouse.getOwnerUserId(),
            warehouse.getName(),
            warehouse.getAddress(),
            warehouse.getStorageUnits().stream().map(WarehouseMapper::toDto).toList(),
            warehouse.getCreatedAt(),
            warehouse.getUpdatedAt(),
            warehouse.getVersion());
    }

    public static StorageUnitResponseDto toDto(StorageUnit storageUnit) {
        return new StorageUnitResponseDto(
            storageUnit.getId(),
            storageUnit.getWarehouseId(),
            storageUnit.getCode(),
            storageUnit.getCapacity(),
            storageUnit.getRemainingCapacity());
    }
}
