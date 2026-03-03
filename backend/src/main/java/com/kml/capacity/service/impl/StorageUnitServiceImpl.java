package com.kml.capacity.service.impl;

import com.kml.capacity.dto.StorageUnitResponseDto;
import com.kml.capacity.mapper.StorageUnitMapper;
import com.kml.capacity.service.StorageUnitService;
import com.kml.domain.warehouse.StorageUnit;
import com.kml.domain.warehouse.Warehouse;
import com.kml.infra.StorageUnitInventoryAssignmentRepository;
import com.kml.infra.StorageUnitRepository;
import com.kml.infra.WarehouseRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StorageUnitServiceImpl implements StorageUnitService {

  private final StorageUnitRepository storageUnitRepository;
  private final WarehouseRepository warehouseRepository;
  private final StorageUnitInventoryAssignmentRepository assignmentRepository;

  public StorageUnitServiceImpl(
      StorageUnitRepository storageUnitRepository,
      WarehouseRepository warehouseRepository,
      StorageUnitInventoryAssignmentRepository assignmentRepository) {

    this.storageUnitRepository = storageUnitRepository;
    this.warehouseRepository = warehouseRepository;
    this.assignmentRepository = assignmentRepository;
  }

  @Override
  @Transactional
  public StorageUnitResponseDto createStorageUnit(String code, Long warehouseId, int capacity) {
    if (warehouseId == null) {
      throw new IllegalArgumentException("warehouseId is required");
    }
    if (code == null || code.isBlank()) {
      throw new IllegalArgumentException("code is required");
    }

    boolean codeExistsInWarehouse =
        storageUnitRepository.findByWarehouse_IdAndCode(warehouseId, code).isPresent();
    if (codeExistsInWarehouse) {
      throw new IllegalArgumentException("StorageUnit code already exists in warehouse");
    }

    Warehouse warehouse =
        warehouseRepository
            .findById(warehouseId)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));

    StorageUnit storageUnit = new StorageUnit(code, capacity);
    warehouse.addStorageUnit(storageUnit);

    Warehouse savedWarehouse = warehouseRepository.save(warehouse);

    StorageUnit savedStorageUnit =
        savedWarehouse.getStorageUnits().stream()
            .filter(u -> u.getCode().equals(code))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("StorageUnit not saved correctly"));

    return StorageUnitMapper.toDto(savedStorageUnit);
  }

  @Override
  @Transactional(readOnly = true)
  public List<StorageUnitResponseDto> getStorageUnitsByWarehouseId(Long warehouseId) {
    if (warehouseId == null) {
      throw new IllegalArgumentException("warehouseId is required");
    }

    return storageUnitRepository.findByWarehouse_Id(warehouseId).stream()
        .map(StorageUnitMapper::toDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public StorageUnitResponseDto getStorageUnitById(Long id) {
    StorageUnit unit =
        storageUnitRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("StorageUnit not found"));

    return StorageUnitMapper.toDto(unit);
  }

  @Override
  @Transactional(readOnly = true)
  public StorageUnitResponseDto getStorageUnitByCode(String code) {
    if (code == null || code.isBlank()) {
      throw new IllegalArgumentException("code is required");
    }

    StorageUnit unit =
        storageUnitRepository
            .findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("StorageUnit not found"));

    return StorageUnitMapper.toDto(unit);
  }

  @Override
  @Transactional(readOnly = true)
  public StorageUnitResponseDto getStorageUnitByWarehouseIdAndCode(Long warehouseId, String code) {
    if (warehouseId == null) {
      throw new IllegalArgumentException("warehouseId is required");
    }
    if (code == null || code.isBlank()) {
      throw new IllegalArgumentException("code is required");
    }

    StorageUnit unit =
        storageUnitRepository
            .findByWarehouse_IdAndCode(warehouseId, code)
            .orElseThrow(() -> new IllegalArgumentException("StorageUnit not found"));

    return StorageUnitMapper.toDto(unit);
  }

  @Override
  @Transactional
  public void deleteStorageUnit(Long id) {
    StorageUnit storageUnit =
        storageUnitRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("StorageUnit not found"));

    if (assignmentRepository.existsByStorageUnit_Id(id)) {
      throw new IllegalStateException(
          "Cannot delete StorageUnit with existing inventory assignments");
    }

    storageUnitRepository.delete(storageUnit);
  }
}
