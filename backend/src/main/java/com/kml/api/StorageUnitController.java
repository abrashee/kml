package com.kml.api;

import com.kml.capacity.dto.StorageUnitRequestDto;
import com.kml.capacity.dto.StorageUnitResponseDto;
import com.kml.capacity.service.StorageUnitService;
import jakarta.validation.Valid;
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
  public ResponseEntity<List<StorageUnitResponseDto>> getStorageUnitByWarehouseId(
      @RequestParam Long warehouseId) {
    return ResponseEntity.ok(storageUnitService.getStorageUnitsByWarehouseId(warehouseId));
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/{id}")
  public ResponseEntity<StorageUnitResponseDto> getStorageUnitById(@PathVariable Long id) {
    return ResponseEntity.ok(storageUnitService.getStorageUnitById(id));
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/by-code")
  public ResponseEntity<StorageUnitResponseDto> getStorageUnitByCode(@RequestParam String code) {
    return ResponseEntity.ok(storageUnitService.getStorageUnitByCode(code));
  }
}
