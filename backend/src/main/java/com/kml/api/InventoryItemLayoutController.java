package com.kml.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kml.capacity.dto.StorageUnitInventoryAssignmentDto;
import com.kml.capacity.service.InventoryItemLayoutService;

@RestController
@RequestMapping("/api/v1/storage-units")
public class InventoryItemLayoutController {

  private final InventoryItemLayoutService inventoryItemLayoutService;

  public InventoryItemLayoutController(InventoryItemLayoutService inventoryItemLayoutService) {
    this.inventoryItemLayoutService = inventoryItemLayoutService;
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/assignments")
  public ResponseEntity<List<StorageUnitInventoryAssignmentDto>> getWarehouseLayout(
      @RequestParam("warehouseId") Long warehouseId) {

    if (warehouseId == null) {
      throw new IllegalArgumentException("warehouseId is required");
    }

    List<StorageUnitInventoryAssignmentDto> dtos =
        inventoryItemLayoutService.getWarehouseLayout(warehouseId);
    return ResponseEntity.ok(dtos);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/{storageUnitId}/assignments")
  public ResponseEntity<List<StorageUnitInventoryAssignmentDto>> getStorageUnitLayout(
      @PathVariable("storageUnitId") Long storageUnitId) {

    if (storageUnitId == null) {
      throw new IllegalArgumentException("storageUnitId is required");
    }

    List<StorageUnitInventoryAssignmentDto> dtos =
        inventoryItemLayoutService.getStorageUnitLayout(storageUnitId);
    return ResponseEntity.ok(dtos);
  }
}
