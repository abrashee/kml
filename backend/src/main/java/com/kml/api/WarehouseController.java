package com.kml.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kml.capacity.dto.WarehouseRequestDto;
import com.kml.capacity.dto.WarehouseResponseDto;
import com.kml.capacity.security.AuthorizationService;
import com.kml.capacity.security.HardcodedUserContext;
import com.kml.capacity.service.WarehouseService;
import com.kml.domain.user.User;
import com.kml.domain.warehouse.Warehouse;

import jakarta.validation.Valid;

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
    User currentUser = HardcodedUserContext.getCurrentUser();
    if (!AuthorizationService.canCreateWarehouse(currentUser)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    Warehouse warehouse =
        this.warehouseService.createWarehouse(
            warehouseRequestDto.getName(), warehouseRequestDto.getAddress());
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(warehouse));
  }

  // Get Warehouse by Id
  @GetMapping("/{id}")
  public ResponseEntity<WarehouseResponseDto> getWarehouseById(@PathVariable Long id) {
    return warehouseService
        .getWarehouseById(id)
        .map(warehouse -> ResponseEntity.ok(mapToResponseDto(warehouse)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Get Warehouse by Name
  @GetMapping("/by-name")
  public ResponseEntity<WarehouseResponseDto> getWarehouseByName(@RequestParam String name) {
    return warehouseService
        .getWarehouseByName(name)
        .map(warehouse -> ResponseEntity.ok(mapToResponseDto(warehouse)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Get All warehouse
  @GetMapping
  public ResponseEntity<List<WarehouseResponseDto>> getAllWarehouses() {
    List<Warehouse> warehouses = this.warehouseService.getAllWarehouses();

    List<WarehouseResponseDto> dtos =
        warehouses.stream().map(this::mapToResponseDto).collect(Collectors.toList());

    return ResponseEntity.ok(dtos);
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
