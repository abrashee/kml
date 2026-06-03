package com.kml.warehouse.dto;

import java.util.List;

import com.kml.warehouse.storageUnit.dto.StorageUnitLayoutDto;

public record WarehouseLayoutDto(
    Long id, String name, String address, List<StorageUnitLayoutDto> storageUnits) {}
