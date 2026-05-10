package com.kml.capacity.dto.warehouse;

import java.util.List;

import com.kml.capacity.dto.storageUnit.StorageUnitLayoutDto;

public record WarehouseLayoutDto(
    Long id, String name, String address, List<StorageUnitLayoutDto> storageUnits) {}
