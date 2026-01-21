package com.kml.capacity.service;

import java.util.List;
import java.util.Optional;

import com.kml.domain.warehouse.StorageUnitInventoryAssignment;

public interface StorageUnitInventoryAssignmentService {

  // Create
  Optional<StorageUnitInventoryAssignment> createStorageUnitInventoryAssignment(
      Long storageUnitId, Long inventoryItemId, int assignedQuantity);

  // Get
  List<StorageUnitInventoryAssignment> getAllStorageUnitInventoryItems();

  List<StorageUnitInventoryAssignment> getByStorageUnitId(Long storageUnitId);

  Optional<StorageUnitInventoryAssignment> getByStorageUnitIdAndInventoryItemId(
      Long storageUnitId, Long inventoryItemId);

  // Update
  Optional<StorageUnitInventoryAssignment> updateStorageUnitInventoryAssignment(
      Long assignmentId, int newQuantity);

  // Delete
  void deleteStorageUnitInventoryItemAssignment(Long id);
}
