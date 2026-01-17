package com.kml.api;

import com.kml.capacity.dto.StorageUnitRequestDto;
import com.kml.capacity.dto.StorageUnitResponseDto;
import com.kml.capacity.service.StorageUnitService;
import com.kml.domain.warehouse.StorageUnit;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/storageunits")
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
