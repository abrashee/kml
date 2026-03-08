package com.kml.capacity.dto.storageUnit;

import java.util.List;

import com.kml.capacity.dto.inventory.InventoryItemLayoutDto;

public record StorageUnitLayoutDto(
    Long id,
    String code,
    Long warehouseId,
    int capacity,
    List<InventoryItemLayoutDto> inventoryItems) {}
