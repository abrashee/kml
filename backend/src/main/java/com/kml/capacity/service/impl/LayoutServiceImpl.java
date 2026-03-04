package com.kml.capacity.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kml.capacity.dto.StorageUnitInventoryAssignmentDto;
import com.kml.capacity.mapper.StorageUnitMapper;
import com.kml.capacity.service.LayoutService;
import com.kml.infra.StorageUnitInventoryAssignmentRepository;
import com.kml.infra.StorageUnitRepository;
import com.kml.infra.WarehouseRepository;

@Service
public class LayoutServiceImpl implements LayoutService {

  private final WarehouseRepository warehouseRepository;
  private final StorageUnitRepository storageUnitRepository;
  private final StorageUnitInventoryAssignmentRepository assignmentRepository;

  public LayoutServiceImpl(
      WarehouseRepository warehouseRepository,
      StorageUnitRepository storageUnitRepository,
      StorageUnitInventoryAssignmentRepository assignmentRepository) {
    this.warehouseRepository = warehouseRepository;
    this.storageUnitRepository = storageUnitRepository;
    this.assignmentRepository = assignmentRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<StorageUnitInventoryAssignmentDto> getWarehouseLayout(Long warehouseId) {
    warehouseRepository
        .findById(warehouseId)
        .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));

    List<Long> storageUnitIds =
        storageUnitRepository.findByWarehouse_Id(warehouseId).stream().map(u -> u.getId()).toList();

    List<StorageUnitInventoryAssignmentDto> dtos = new ArrayList<>();
    for (Long unitId : storageUnitIds) {
      assignmentRepository
          .findByStorageUnit_Id(unitId)
          .forEach(assignment -> dtos.add(StorageUnitMapper.toDto(assignment)));
    }

    return dtos;
  }

  @Override
  @Transactional(readOnly = true)
  public List<StorageUnitInventoryAssignmentDto> getStorageUnitLayout(Long storageUnitId) {
    storageUnitRepository
        .findById(storageUnitId)
        .orElseThrow(() -> new IllegalArgumentException("StorageUnit not found"));

    return assignmentRepository.findByStorageUnit_Id(storageUnitId).stream()
        .map(StorageUnitMapper::toDto)
        .toList();
  }
}
