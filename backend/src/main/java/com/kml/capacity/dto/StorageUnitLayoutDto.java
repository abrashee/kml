package com.kml.capacity.dto;

import java.util.List;

public record StorageUnitLayoutDto(
    Long id,
    String code,
    Long warehouseId,
    int capacity,
    List<InventoryItemLayoutDto> inventoryItems) {}
