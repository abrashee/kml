package com.kml.capacity.service.impl;

import com.kml.capacity.dto.StorageUnitInventoryAssignmentDto;
import com.kml.capacity.mapper.StorageUnitMapper;
import com.kml.capacity.service.StorageUnitInventoryAssignmentService;
import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.warehouse.StorageUnit;
import com.kml.domain.warehouse.StorageUnitInventoryAssignment;
import com.kml.infra.InventoryRepository;
import com.kml.infra.StorageUnitInventoryAssignmentRepository;
import com.kml.infra.StorageUnitRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StorageUnitInventoryAssignmentServiceImpl
    implements StorageUnitInventoryAssignmentService {

  private final InventoryRepository inventoryRepository;
  private final StorageUnitRepository storageUnitRepository;
  private final StorageUnitInventoryAssignmentRepository assignmentRepository;

  public StorageUnitInventoryAssignmentServiceImpl(
      InventoryRepository inventoryRepository,
      StorageUnitRepository storageUnitRepository,
      StorageUnitInventoryAssignmentRepository assignmentRepository) {
    this.inventoryRepository = inventoryRepository;
    this.storageUnitRepository = storageUnitRepository;
    this.assignmentRepository = assignmentRepository;
  }

  @Override
  @Transactional
  public StorageUnitInventoryAssignmentDto createStorageUnitInventoryAssignment(
      Long storageUnitId, Long inventoryItemId, int assignedQuantity) {

    StorageUnit storageUnit =
        storageUnitRepository
            .findById(storageUnitId)
            .orElseThrow(() -> new IllegalArgumentException("StorageUnit not found"));

    InventoryItem inventoryItem =
        inventoryRepository
            .findById(inventoryItemId)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));

    boolean exists =
        assignmentRepository
            .findByStorageUnit_IdAndInventoryItem_Id(storageUnitId, inventoryItemId)
            .isPresent();
    if (exists) {
      throw new IllegalArgumentException("Assignment already exists");
    }

    StorageUnitInventoryAssignment assignment =
        new StorageUnitInventoryAssignment(storageUnit, inventoryItem, assignedQuantity);

    StorageUnitInventoryAssignment saved = assignmentRepository.save(assignment);
    return StorageUnitMapper.toDto(saved);
  }

  @Override
  public List<StorageUnitInventoryAssignmentDto> getAllStorageUnitInventoryItems() {
    return assignmentRepository.findAll().stream().map(StorageUnitMapper::toDto).toList();
  }

  @Override
  public List<StorageUnitInventoryAssignmentDto> getByStorageUnitId(Long storageUnitId) {
    storageUnitRepository
        .findById(storageUnitId)
        .orElseThrow(() -> new IllegalArgumentException("StorageUnit not found"));

    return assignmentRepository.findByStorageUnit_Id(storageUnitId).stream()
        .map(StorageUnitMapper::toDto)
        .toList();
  }

  @Override
  public StorageUnitInventoryAssignmentDto getByStorageUnitIdAndInventoryItemId(
      Long storageUnitId, Long inventoryItemId) {

    StorageUnitInventoryAssignment assignment =
        assignmentRepository
            .findByStorageUnit_IdAndInventoryItem_Id(storageUnitId, inventoryItemId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

    return StorageUnitMapper.toDto(assignment);
  }

  @Override
  @Transactional
  public StorageUnitInventoryAssignmentDto updateStorageUnitInventoryAssignment(
      Long assignmentId, int newQuantity) {

    StorageUnitInventoryAssignment assignment =
        assignmentRepository
            .findById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

    assignment.updateAssignedQuantity(newQuantity);

    StorageUnitInventoryAssignment saved = assignmentRepository.save(assignment);
    return StorageUnitMapper.toDto(saved);
  }

  @Override
  @Transactional
  public void deleteStorageUnitInventoryItemAssignment(Long id) {
    if (!assignmentRepository.existsById(id)) {
      throw new IllegalArgumentException("Assignment not found");
    }
    assignmentRepository.deleteById(id);
  }
}
