package com.kml.capacity.dto;

import java.util.List;

public record WarehouseLayoutDto(
    Long id, String name, String address, List<StorageUnitLayoutDto> storageUnits) {}
