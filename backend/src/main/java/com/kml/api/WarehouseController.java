package com.kml.api;

import com.kml.capacity.dto.WarehouseRequestDto;
import com.kml.capacity.dto.WarehouseResponseDto;
import com.kml.capacity.service.WarehouseService;
import com.kml.domain.warehouse.Warehouse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehouseController {

  private final WarehouseService warehouseService;

  public WarehouseController(WarehouseService warehouseService) {
    this.warehouseService = warehouseService;
  }

  // Create Warehosue
  @PostMapping
  public ResponseEntity<WarehouseResponseDto> createWarehouse(
      @RequestBody @Valid WarehouseRequestDto warehouseRequestDto) {
    Warehouse warehouse =
        this.warehouseService.createWarehouse(
            warehouseRequestDto.getName(), warehouseRequestDto.getAddress());
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(warehouse));
  }

  // Mapping Respose DTO
  private WarehouseResponseDto mapToResponseDto(Warehouse warehouse) {

    WarehouseResponseDto responseDto = new WarehouseResponseDto();
    responseDto.setId(warehouse.getId());
    responseDto.setName(warehouse.getName());
    responseDto.setAddress(warehouse.getAddress());

    return responseDto;
  }
}
