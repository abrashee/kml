package com.kml.warehouse.storageUnit.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kml.inventory.entity.InventoryItem;
import com.kml.inventory.repository.InventoryRepository;
import com.kml.security.CurrentUserProvider;
import com.kml.user.entity.User;
import com.kml.warehouse.storageUnit.dto.StorageUnitInventoryAssignmentDto;
import com.kml.warehouse.storageUnit.entity.StorageUnit;
import com.kml.warehouse.storageUnit.entity.StorageUnitInventoryAssignment;
import com.kml.warehouse.storageUnit.mapper.StorageUnitMapper;
import com.kml.warehouse.storageUnit.repository.StorageUnitInventoryAssignmentRepository;
import com.kml.warehouse.storageUnit.repository.StorageUnitRepository;

@Service
public class StorageUnitInventoryAssignmentServiceImpl
    implements StorageUnitInventoryAssignmentService {

  private final InventoryRepository inventoryRepository;
  private final StorageUnitRepository storageUnitRepository;
  private final StorageUnitInventoryAssignmentRepository assignmentRepository;
  private final CurrentUserProvider currentUserProvider;

  public StorageUnitInventoryAssignmentServiceImpl(
      InventoryRepository inventoryRepository,
      StorageUnitRepository storageUnitRepository,
      StorageUnitInventoryAssignmentRepository assignmentRepository,
      CurrentUserProvider currentUserProvider) {

    this.inventoryRepository = inventoryRepository;
    this.storageUnitRepository = storageUnitRepository;
    this.assignmentRepository = assignmentRepository;
    this.currentUserProvider = currentUserProvider;
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

    if (assignmentRepository
        .findByStorageUnit_IdAndInventoryItem_Id(storageUnitId, inventoryItemId)
        .isPresent()) {
      throw new IllegalArgumentException("Assignment already exists");
    }

    User user = currentUserProvider.getCurrentUser();

    StorageUnitInventoryAssignment assignment =
        StorageUnitInventoryAssignment.create(user, storageUnit, inventoryItem, assignedQuantity);

    StorageUnitInventoryAssignment saved = assignmentRepository.save(assignment);

    return StorageUnitMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<StorageUnitInventoryAssignmentDto> getAllStorageUnitInventoryItems() {
    return assignmentRepository.findAll().stream().map(StorageUnitMapper::toDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<StorageUnitInventoryAssignmentDto> getByStorageUnitId(Long storageUnitId) {

    storageUnitRepository
        .findById(storageUnitId)
        .orElseThrow(() -> new IllegalArgumentException("StorageUnit not found"));

    return assignmentRepository.findByStorageUnit_Id(storageUnitId).stream()
        .map(StorageUnitMapper::toDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
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
