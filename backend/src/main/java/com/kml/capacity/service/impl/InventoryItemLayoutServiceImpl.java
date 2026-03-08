package com.kml.capacity.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kml.capacity.dto.storageUnit.StorageUnitInventoryAssignmentDto;
import com.kml.capacity.mapper.StorageUnitMapper;
import com.kml.capacity.security.CurrentUserProvider;
import com.kml.capacity.service.InventoryItemLayoutService;
import com.kml.domain.user.User;
import com.kml.infra.StorageUnitInventoryAssignmentRepository;
import com.kml.infra.StorageUnitRepository;
import com.kml.infra.WarehouseRepository;

@Service
public class InventoryItemLayoutServiceImpl implements InventoryItemLayoutService {

  private final WarehouseRepository warehouseRepository;
  private final StorageUnitRepository storageUnitRepository;
  private final StorageUnitInventoryAssignmentRepository assignmentRepository;
  private final CurrentUserProvider currentUserProvider;

  public InventoryItemLayoutServiceImpl(
      WarehouseRepository warehouseRepository,
      StorageUnitRepository storageUnitRepository,
      StorageUnitInventoryAssignmentRepository assignmentRepository,
      CurrentUserProvider currentUserProvider) {
    this.warehouseRepository = warehouseRepository;
    this.storageUnitRepository = storageUnitRepository;
    this.assignmentRepository = assignmentRepository;
    this.currentUserProvider = currentUserProvider;
  }

  @Override
  @Transactional(readOnly = true)
  public List<StorageUnitInventoryAssignmentDto> getWarehouseLayout(Long warehouseId) {
    User currentUser = currentUserProvider.getCurrentUser();

    warehouseRepository
        .findById(warehouseId)
        .filter(w -> w.getOwner().getId().equals(currentUser.getId()))
        .orElseThrow(() -> new IllegalArgumentException("Warehouse not found or access denied"));

    List<Long> storageUnitIds =
        storageUnitRepository.findByWarehouse_Id(warehouseId).stream().map(u -> u.getId()).toList();

    return storageUnitIds.stream()
        .flatMap(
            unitId ->
                assignmentRepository.findByStorageUnit_Id(unitId).stream()
                    .map(StorageUnitMapper::toDto))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<StorageUnitInventoryAssignmentDto> getStorageUnitLayout(Long storageUnitId) {
    User currentUser = currentUserProvider.getCurrentUser();

    storageUnitRepository
        .findById(storageUnitId)
        .filter(su -> su.getOwner().getId().equals(currentUser.getId()))
        .orElseThrow(() -> new IllegalArgumentException("StorageUnit not found or access denied"));

    return assignmentRepository.findByStorageUnit_Id(storageUnitId).stream()
        .map(StorageUnitMapper::toDto)
        .toList();
  }
}
