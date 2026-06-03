package com.kml.warehouse.storageUnit.dto;

import java.util.List;

import com.kml.inventory.dto.InventoryItemLayoutDto;

public record StorageUnitLayoutDto(
    Long id,
    String code,
    Long warehouseId,
    int capacity,
    List<InventoryItemLayoutDto> inventoryItems) {}
