package com.kml.warehouse.storageUnit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.warehouse.storageUnit.entity.StorageUnitInventoryAssignment;

public interface StorageUnitInventoryAssignmentRepository
    extends JpaRepository<StorageUnitInventoryAssignment, Long> {

  List<StorageUnitInventoryAssignment> findByInventoryItem_Id(Long inventoryItemId);

  List<StorageUnitInventoryAssignment> findByStorageUnit_Id(Long storageUnitId);

  Optional<StorageUnitInventoryAssignment> findByStorageUnit_IdAndInventoryItem_Id(
      Long storageUnitId, Long inventoryItemId);

  boolean existsByInventoryItem_Id(Long inventoryItemId);

  boolean existsByStorageUnit_Id(Long storageUnitId);

  int countByStorageUnit_Id(Long storageUnitId);
}
