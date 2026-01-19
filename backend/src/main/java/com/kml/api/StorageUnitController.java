package com.kml.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kml.capacity.dto.StorageUnitRequestDto;
import com.kml.capacity.dto.StorageUnitResponseDto;
import com.kml.capacity.service.StorageUnitService;
import com.kml.domain.warehouse.StorageUnit;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/storage-units")
public class StorageUnitController {
  private final StorageUnitService storageUnitService;

  public StorageUnitController(StorageUnitService storageUnitService) {
    this.storageUnitService = storageUnitService;
  }

  @PostMapping
  public ResponseEntity<StorageUnitResponseDto> createStorageUnit(
      @Valid @RequestBody StorageUnitRequestDto requestDto) {
    StorageUnit storageUnit =
        this.storageUnitService.createStorageUnit(
            requestDto.getCode(), requestDto.getWarehouseId(), requestDto.getCapacity());
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(storageUnit));
  }

  // Get
  @GetMapping("/by-warehouse")
  public ResponseEntity<List<StorageUnitResponseDto>> getStorageUnitByWarehouseId(
      @RequestParam Long warehouseId) {

    List<StorageUnitResponseDto> responseDto =
        storageUnitService.getStorageUnitsByWarehouseId(warehouseId).stream()
            .map(this::mapToResponseDto)
            .toList();

    return ResponseEntity.ok(responseDto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<StorageUnitResponseDto> getStorageUnitById(@PathVariable Long id) {
    return storageUnitService
        .getStorageUnitById(id)
        .map(unit -> ResponseEntity.ok(mapToResponseDto(unit)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/by-code")
  public ResponseEntity<StorageUnitResponseDto> getStorageUnitByCode(@RequestParam String code) {
    return storageUnitService
        .getStorageUnitByCode(code)
        .map(unit -> ResponseEntity.ok(mapToResponseDto(unit)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  private StorageUnitResponseDto mapToResponseDto(StorageUnit storageUnit) {
    StorageUnitResponseDto responseDto = new StorageUnitResponseDto();
    responseDto.setId(storageUnit.getId());
    responseDto.setCode(storageUnit.getCode());
    responseDto.setWarehouseId(
        storageUnit.getWarehouse() != null ? storageUnit.getWarehouse().getId() : null);
    responseDto.setCapacity(storageUnit.getCapacity());

    return responseDto;
  }
}
