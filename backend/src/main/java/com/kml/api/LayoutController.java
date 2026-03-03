package com.kml.api;

import com.kml.capacity.dto.StorageUnitInventoryAssignmentDto;
import com.kml.capacity.mapper.StorageUnitMapper;
import com.kml.capacity.service.LayoutService;
import com.kml.domain.warehouse.StorageUnitInventoryAssignment;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/storage-units")
public class LayoutController {

  private final LayoutService layoutService;

  public LayoutController(LayoutService layoutService) {
    this.layoutService = layoutService;
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/assignments")
  public ResponseEntity<List<StorageUnitInventoryAssignmentDto>> getWarehouseLayout(
      @RequestParam Long warehouseId) {

    List<StorageUnitInventoryAssignment> assignments =
        layoutService.getWarehouseLayout(warehouseId);

    List<StorageUnitInventoryAssignmentDto> dtos =
        assignments.stream().map(StorageUnitMapper::toDto).toList();

    return ResponseEntity.ok(dtos);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/{storageUnitId}/assignments")
  public ResponseEntity<List<StorageUnitInventoryAssignmentDto>> getStorageUnitLayout(
      @RequestParam Long storageUnitId) {

    List<StorageUnitInventoryAssignment> assignments =
        layoutService.getStorageUnitLayout(storageUnitId);

    List<StorageUnitInventoryAssignmentDto> dtos =
        assignments.stream().map(StorageUnitMapper::toDto).toList();

    return ResponseEntity.ok(dtos);
  }
}
