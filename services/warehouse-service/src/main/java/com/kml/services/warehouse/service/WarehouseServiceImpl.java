package com.kml.services.warehouse.service;

import com.kml.services.warehouse.dto.StorageUnitResponseDto;
import com.kml.services.warehouse.dto.StorageUnitRequestDto;
import com.kml.services.warehouse.dto.WarehouseRequestDto;
import com.kml.services.warehouse.dto.WarehouseResponseDto;
import com.kml.services.warehouse.entity.StorageUnit;
import com.kml.services.warehouse.entity.Warehouse;
import com.kml.services.warehouse.mapper.WarehouseMapper;
import com.kml.services.warehouse.repository.StorageUnitRepository;
import com.kml.services.warehouse.repository.WarehouseRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final StorageUnitRepository storageUnitRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, StorageUnitRepository storageUnitRepository) {
        this.warehouseRepository = warehouseRepository;
        this.storageUnitRepository = storageUnitRepository;
    }

    @Override
    @Transactional
    public WarehouseResponseDto createWarehouse(WarehouseRequestDto request) {
        warehouseRepository.findByName(request.name()).ifPresent(existing -> {
            throw new IllegalArgumentException("Warehouse name already exists");
        });

        Warehouse warehouse = new Warehouse(request.ownerUserId(), request.name(), request.address());
        if (request.storageUnits() != null) {
            request.storageUnits().forEach(unit -> warehouse.addStorageUnit(new StorageUnit(unit.code(), unit.capacity())));
        }
        return WarehouseMapper.toDto(warehouseRepository.save(warehouse));
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponseDto getWarehouse(Long id) {
        return warehouseRepository.findById(id)
            .map(WarehouseMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseResponseDto> getWarehouses(Long ownerUserId) {
        List<Warehouse> warehouses = ownerUserId != null
            ? warehouseRepository.findByOwnerUserId(ownerUserId)
            : warehouseRepository.findAll();
        return warehouses.stream().map(WarehouseMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StorageUnitResponseDto> getStorageUnits(Long warehouseId) {
        return storageUnitRepository.findByWarehouse_Id(warehouseId).stream()
            .map(WarehouseMapper::toDto)
            .toList();
    }

    @Override
    @Transactional
    public StorageUnitResponseDto addStorageUnit(Long warehouseId, StorageUnitRequestDto request) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        StorageUnit storageUnit = new StorageUnit(request.code(), request.capacity());
        warehouse.addStorageUnit(storageUnit);
        warehouseRepository.save(warehouse);
        return WarehouseMapper.toDto(storageUnit);
    }
}
