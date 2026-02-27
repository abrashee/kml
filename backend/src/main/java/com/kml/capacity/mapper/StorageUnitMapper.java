package com.kml.capacity.mapper;

import com.kml.capacity.dto.StorageUnitInventoryAssignmentDto;
import com.kml.capacity.dto.StorageUnitResponseDto;
import com.kml.domain.warehouse.StorageUnit;
import com.kml.domain.warehouse.StorageUnitInventoryAssignment;

public final class StorageUnitMapper {

  private StorageUnitMapper() {}

  public static StorageUnitResponseDto toDto(StorageUnit entity) {
    if (entity == null) {
      return null;
    }

    StorageUnitResponseDto dto = new StorageUnitResponseDto();
    dto.setId(entity.getId());
    dto.setCode(entity.getCode());
    dto.setCapacity(entity.getCapacity());
    dto.setWarehouseId(entity.getWarehouse() != null ? entity.getWarehouse().getId() : null);
    return dto;
  }

  public static StorageUnitInventoryAssignmentDto toDto(StorageUnitInventoryAssignment entity) {
    if (entity == null) {
      return null;
    }

    StorageUnitInventoryAssignmentDto dto = new StorageUnitInventoryAssignmentDto();

    dto.setStorageUnitId(entity.getStorageUnit().getId());
    dto.setStorageUnitCode(entity.getStorageUnit().getCode());

    dto.setInventoryItemId(entity.getInventoryItem().getId());
    dto.setInventoryItemSku(entity.getInventoryItem().getSku());
    dto.setInventoryItemName(entity.getInventoryItem().getName());

    dto.setAssignedQuantity(entity.getAssignedQuantity());
    return dto;
  }
}
