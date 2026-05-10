package com.kml.capacity.service.impl;

import com.kml.api.error.OwnershipException;
import com.kml.capacity.dto.storageUnit.StorageUnitResponseDto;
import com.kml.capacity.mapper.StorageUnitMapper;
import com.kml.capacity.security.CurrentUserProvider;
import com.kml.capacity.service.StorageUnitService;
import com.kml.domain.user.User;
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
  private final CurrentUserProvider currentUserProvider;

  public StorageUnitServiceImpl(
      StorageUnitRepository storageUnitRepository,
      WarehouseRepository warehouseRepository,
      StorageUnitInventoryAssignmentRepository assignmentRepository,
      CurrentUserProvider currentUserProvider) {
    this.storageUnitRepository = storageUnitRepository;
    this.warehouseRepository = warehouseRepository;
    this.assignmentRepository = assignmentRepository;
    this.currentUserProvider = currentUserProvider;
  }

  @Override
  @Transactional
  public StorageUnitResponseDto createStorageUnit(String code, Long warehouseId, int capacity) {
    if (warehouseId == null) throw new IllegalArgumentException("warehouseId is required");
    if (code == null || code.isBlank()) throw new IllegalArgumentException("code is required");

    Warehouse warehouse =
        warehouseRepository
            .findById(warehouseId)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));

    enforceOwnership(warehouse, currentUserProvider.getCurrentUser());

    boolean codeExists =
        storageUnitRepository.findByWarehouse_IdAndCode(warehouseId, code).isPresent();
    if (codeExists)
      throw new IllegalArgumentException("StorageUnit code already exists in warehouse");

    User owner = currentUserProvider.getCurrentUser();
    StorageUnit storageUnit = StorageUnit.create(owner, code, capacity);
    warehouse.addStorageUnit(storageUnit);

    Warehouse savedWarehouse = warehouseRepository.save(warehouse);

    StorageUnit savedUnit =
        savedWarehouse.getStorageUnits().stream()
            .filter(u -> u.getCode().equals(code))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("StorageUnit not saved correctly"));

    enforceOwnership(savedUnit, owner);

    return StorageUnitMapper.toDto(savedUnit);
  }

  @Override
  @Transactional(readOnly = true)
  public List<StorageUnitResponseDto> getStorageUnitsByWarehouseId(Long warehouseId) {
    Warehouse warehouse =
        warehouseRepository
            .findById(warehouseId)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
    enforceOwnership(warehouse, currentUserProvider.getCurrentUser());

    User currentUser = currentUserProvider.getCurrentUser();
    return storageUnitRepository.findByWarehouse_Id(warehouseId).stream()
        .filter(u -> u.getOwner().getId().equals(currentUser.getId()))
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
    enforceOwnership(unit, currentUserProvider.getCurrentUser());
    return StorageUnitMapper.toDto(unit);
  }

  @Override
  @Transactional(readOnly = true)
  public StorageUnitResponseDto getStorageUnitByCode(String code) {
    if (code == null || code.isBlank()) throw new IllegalArgumentException("Code is required");

    StorageUnit unit =
        storageUnitRepository
            .findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("StorageUnit not found"));
    enforceOwnership(unit, currentUserProvider.getCurrentUser());
    return StorageUnitMapper.toDto(unit);
  }

  @Override
  @Transactional(readOnly = true)
  public StorageUnitResponseDto getStorageUnitByWarehouseIdAndCode(Long warehouseId, String code) {
    if (warehouseId == null) throw new IllegalArgumentException("warehouseId is required");
    if (code == null || code.isBlank()) throw new IllegalArgumentException("Code is required");

    StorageUnit unit =
        storageUnitRepository
            .findByWarehouse_IdAndCode(warehouseId, code)
            .orElseThrow(() -> new IllegalArgumentException("StorageUnit not found"));
    enforceOwnership(unit, currentUserProvider.getCurrentUser());
    return StorageUnitMapper.toDto(unit);
  }

  @Override
  @Transactional
  public void deleteStorageUnit(Long id) {
    StorageUnit unit =
        storageUnitRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("StorageUnit not found"));
    enforceOwnership(unit, currentUserProvider.getCurrentUser());

    if (assignmentRepository.existsByStorageUnit_Id(id)) {
      throw new IllegalStateException(
          "Cannot delete StorageUnit with existing inventory assignments");
    }

    storageUnitRepository.delete(unit);
  }

  public void enforceOwnership(StorageUnit entity, User user) {
    if (!entity.getOwner().getId().equals(user.getId())) {
      throw new OwnershipException("User does not own this StorageUnit");
    }
  }

  public void enforceOwnership(Warehouse entity, User user) {
    if (!entity.getOwner().getId().equals(user.getId())) {
      throw new OwnershipException("User does not own this Warehouse");
    }
  }
}
