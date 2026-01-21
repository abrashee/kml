package com.kml.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kml.capacity.dto.StorageUnitInventoryAssignmentDto;
import com.kml.capacity.service.LayoutService;
import com.kml.domain.warehouse.StorageUnitInventoryAssignment;

@RestController
@RequestMapping("/api/v1/layouts")
public class LayoutController {

  private final LayoutService layoutService;

  public LayoutController(LayoutService layoutService) {
    this.layoutService = layoutService;
  }

  @GetMapping("/by-warehouse")
  public ResponseEntity<List<StorageUnitInventoryAssignmentDto>> getWarehouseLayout(
      @RequestParam Long warehouseId) {

    List<StorageUnitInventoryAssignment> assignments =
        layoutService.getWarehouseLayout(warehouseId);

    List<StorageUnitInventoryAssignmentDto> dtos =
        assignments.stream().map(this::mapToResponseDto).collect(Collectors.toList());

    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/by-storage-unit")
  public ResponseEntity<List<StorageUnitInventoryAssignmentDto>> getStorageUnitLayout(
      @RequestParam Long storageUnitId) {
    List<StorageUnitInventoryAssignment> assignments =
        layoutService.getStorageUnitLayout(storageUnitId);
    List<StorageUnitInventoryAssignmentDto> dtos =
        assignments.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  private StorageUnitInventoryAssignmentDto mapToResponseDto(
      StorageUnitInventoryAssignment assignment) {
    StorageUnitInventoryAssignmentDto responseDto = new StorageUnitInventoryAssignmentDto();
    responseDto.setStorageUnitId(assignment.getStorageUnit().getId());
    responseDto.setStorageUnitCode(assignment.getStorageUnit().getCode());

    responseDto.setInventoryItemId(assignment.getInventoryItem().getId());
    responseDto.setInventoryItemSku(assignment.getInventoryItem().getSku());
    responseDto.setInventoryItemName(assignment.getInventoryItem().getName());

    responseDto.setAssignedQuantity(assignment.getAssignedQuantity());
    return responseDto;
  }
}
