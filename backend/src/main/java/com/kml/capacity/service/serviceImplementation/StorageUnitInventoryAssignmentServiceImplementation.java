package com.kml.capacity.service.serviceImplementation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kml.capacity.service.StorageUnitInventoryAssignmentService;
import com.kml.domain.inventory.InventoryItem;
import com.kml.domain.warehouse.StorageUnit;
import com.kml.domain.warehouse.StorageUnitInventoryAssignment;
import com.kml.infra.InventoryRepository;
import com.kml.infra.StorageUnitInventoryAssignmentRepository;
import com.kml.infra.StorageUnitRepository;

import jakarta.transaction.Transactional;

@Service
public class StorageUnitInventoryAssignmentServiceImplementation
    implements StorageUnitInventoryAssignmentService {

  private final InventoryRepository inventoryRepository;
  private final StorageUnitRepository storageUnitRepository;
  private final StorageUnitInventoryAssignmentRepository storageUnitInventoryAssignmentRepository;

  public StorageUnitInventoryAssignmentServiceImplementation(
      InventoryRepository inventoryRepository,
      StorageUnitRepository storageUnitRepository,
      StorageUnitInventoryAssignmentRepository storageUnitInventoryAssignmentRepository) {
    this.inventoryRepository = inventoryRepository;
    this.storageUnitRepository = storageUnitRepository;
    this.storageUnitInventoryAssignmentRepository = storageUnitInventoryAssignmentRepository;
  }

  @Override
  @Transactional
  public Optional<StorageUnitInventoryAssignment> createStorageUnitInventoryAssignment(
      Long storageUnitId, Long inventoryItemId, int assignedQuantity) {

    StorageUnit storageUnit =
        storageUnitRepository
            .findById(storageUnitId)
            .orElseThrow(() -> new IllegalArgumentException("StorageUnit not found"));

    InventoryItem inventoryItem =
        inventoryRepository
            .findById(inventoryItemId)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));

    Optional<StorageUnitInventoryAssignment> existing =
        storageUnitInventoryAssignmentRepository.findByStorageUnit_IdAndInventoryItem_Id(
            storageUnitId, inventoryItemId);

    if (existing.isPresent()) {
      throw new IllegalArgumentException("Assignment already exits");
    }

    StorageUnitInventoryAssignment assignment =
        new StorageUnitInventoryAssignment(storageUnit, inventoryItem, assignedQuantity);

    StorageUnitInventoryAssignment saved =
        storageUnitInventoryAssignmentRepository.save(assignment);

    return Optional.of(saved);
  }

  @Override
  public List<StorageUnitInventoryAssignment> getAllStorageUnitInventoryItems() {
    return storageUnitInventoryAssignmentRepository.findAll();
  }

  @Override
  public List<StorageUnitInventoryAssignment> getByStorageUnitId(Long storageUnitId) {
    return storageUnitInventoryAssignmentRepository.findByStorageUnit_Id(storageUnitId);
  }

  @Override
  public Optional<StorageUnitInventoryAssignment> updateStorageUnitInventoryAssignment(
      Long assignmentId, int newQuantity) {

    StorageUnitInventoryAssignment assignment =
        storageUnitInventoryAssignmentRepository
            .findById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

    assignment.updateAssignedQuantity(newQuantity);
    return Optional.of(storageUnitInventoryAssignmentRepository.save(assignment));
  }

  @Override
  public void deleteStorageUnitInventoryItemAssignment(Long id) {
    storageUnitInventoryAssignmentRepository.deleteById(id);
  }

  @Override
  public Optional<StorageUnitInventoryAssignment> getByStorageUnitIdAndInventoryItemId(
      Long storageUnitId, Long inventoryItemId) {
    return this.storageUnitInventoryAssignmentRepository.findByStorageUnit_IdAndInventoryItem_Id(
        storageUnitId, inventoryItemId);
  }
}
