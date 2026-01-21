package com.kml.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kml.domain.warehouse.StorageUnitInventoryAssignment;

public interface StorageUnitInventoryAssignmentRepository
    extends JpaRepository<StorageUnitInventoryAssignment, Long> {

  // Search by StorageUnit Id
  List<StorageUnitInventoryAssignment> findByStorageUnit_Id(Long storageUnitId);

  Optional<StorageUnitInventoryAssignment> findByStorageUnit_IdAndInventoryItem_Id(
      Long storageUnitId, Long inventoryItemId);
}
