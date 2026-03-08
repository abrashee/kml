package com.kml.capacity.service;

import java.util.List;

import com.kml.capacity.dto.storageUnit.StorageUnitInventoryAssignmentDto;

public interface StorageUnitInventoryAssignmentService {

  StorageUnitInventoryAssignmentDto createStorageUnitInventoryAssignment(
      Long storageUnitId, Long inventoryItemId, int assignedQuantity);

  List<StorageUnitInventoryAssignmentDto> getAllStorageUnitInventoryItems();

  List<StorageUnitInventoryAssignmentDto> getByStorageUnitId(Long storageUnitId);

  StorageUnitInventoryAssignmentDto getByStorageUnitIdAndInventoryItemId(
      Long storageUnitId, Long inventoryItemId);

  StorageUnitInventoryAssignmentDto updateStorageUnitInventoryAssignment(
      Long assignmentId, int newQuantity);

  void deleteStorageUnitInventoryItemAssignment(Long id);
}
