package com.kml.warehouse.storageUnit.service;

import java.util.List;

import com.kml.warehouse.storageUnit.dto.StorageUnitInventoryAssignmentDto;

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
