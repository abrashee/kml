package com.kml.capacity.service.impl;

import com.kml.capacity.service.LayoutService;
import com.kml.domain.warehouse.StorageUnit;
import com.kml.domain.warehouse.StorageUnitInventoryAssignment;
import com.kml.infra.StorageUnitInventoryAssignmentRepository;
import com.kml.infra.StorageUnitRepository;
import com.kml.infra.WarehouseRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LayoutServiceImpl implements LayoutService {
  private final WarehouseRepository warehouseRepository;
  private final StorageUnitRepository storageUnitRepository;
  private final StorageUnitInventoryAssignmentRepository storageUnitInventoryAssignmentRepository;

  public LayoutServiceImpl(
      WarehouseRepository warehouseRepository,
      StorageUnitRepository storageUnitRepository,
      StorageUnitInventoryAssignmentRepository storageUnitInventoryAssignmentRepository) {
    this.warehouseRepository = warehouseRepository;
    this.storageUnitRepository = storageUnitRepository;
    this.storageUnitInventoryAssignmentRepository = storageUnitInventoryAssignmentRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<StorageUnitInventoryAssignment> getWarehouseLayout(Long warehouseId) {

    warehouseRepository
        .findById(warehouseId)
        .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));

    List<StorageUnit> storageUnits = this.storageUnitRepository.findByWarehouse_Id(warehouseId);

    List<StorageUnitInventoryAssignment> assignments = new ArrayList<>();

    for (var unit : storageUnits) {
      assignments.addAll(
          storageUnitInventoryAssignmentRepository.findByStorageUnit_Id(unit.getId()));
    }

    return assignments;
  }

  @Override
  @Transactional(readOnly = true)
  public List<StorageUnitInventoryAssignment> getStorageUnitLayout(Long storageUnitId) {

    storageUnitRepository
        .findById(storageUnitId)
        .orElseThrow(() -> new IllegalArgumentException("StorageUnit not found"));

    List<StorageUnitInventoryAssignment> assignments =
        this.storageUnitInventoryAssignmentRepository.findByStorageUnit_Id(storageUnitId);

    return assignments;
  }
}
