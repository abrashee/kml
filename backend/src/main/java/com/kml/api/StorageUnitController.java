package com.kml.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kml.capacity.dto.storageUnit.StorageUnitRequestDto;
import com.kml.capacity.dto.storageUnit.StorageUnitResponseDto;
import com.kml.capacity.service.StorageUnitService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/storage-units")
public class StorageUnitController {

  private final StorageUnitService storageUnitService;

  public StorageUnitController(StorageUnitService storageUnitService) {
    this.storageUnitService = storageUnitService;
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @PostMapping
  public ResponseEntity<StorageUnitResponseDto> createStorageUnit(
      @Valid @RequestBody StorageUnitRequestDto requestDto) {

    StorageUnitResponseDto storageUnit =
        storageUnitService.createStorageUnit(
            requestDto.getCode(), requestDto.getWarehouseId(), requestDto.getCapacity());

    return ResponseEntity.status(HttpStatus.CREATED).body(storageUnit);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/by-warehouse")
  public ResponseEntity<List<StorageUnitResponseDto>> getStorageUnitsByWarehouseId(
      @RequestParam Long warehouseId) {

    List<StorageUnitResponseDto> dtos =
        storageUnitService.getStorageUnitsByWarehouseId(warehouseId);
    return ResponseEntity.ok(dtos);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/{id}")
  public ResponseEntity<StorageUnitResponseDto> getStorageUnitById(@PathVariable Long id) {

    StorageUnitResponseDto dto = storageUnitService.getStorageUnitById(id);
    return ResponseEntity.ok(dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/by-code")
  public ResponseEntity<StorageUnitResponseDto> getStorageUnitByCode(@RequestParam String code) {

    StorageUnitResponseDto dto = storageUnitService.getStorageUnitByCode(code);
    return ResponseEntity.ok(dto);
  }
}
