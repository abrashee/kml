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

import com.kml.capacity.dto.WarehouseRequestDto;
import com.kml.capacity.dto.WarehouseResponseDto;
import com.kml.capacity.mapper.WarehouseMapper;
import static com.kml.capacity.mapper.WarehouseMapper.toDto;
import com.kml.capacity.service.WarehouseService;
import com.kml.domain.warehouse.Warehouse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehouseController {

  private final WarehouseService warehouseService;

  public WarehouseController(WarehouseService warehouseService) {
    this.warehouseService = warehouseService;
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @PostMapping
  public ResponseEntity<WarehouseResponseDto> createWarehouse(
      @RequestBody @Valid WarehouseRequestDto warehouseRequestDto) {

    Warehouse warehouse =
        warehouseService.createWarehouse(
            warehouseRequestDto.getName(), warehouseRequestDto.getAddress());

    return ResponseEntity.status(HttpStatus.CREATED).body(toDto(warehouse));
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/{id}")
  public ResponseEntity<WarehouseResponseDto> getWarehouseById(@PathVariable Long id) {

    return warehouseService
        .getWarehouseById(id)
        .map(w -> ResponseEntity.ok(toDto(w)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping("/by-name")
  public ResponseEntity<WarehouseResponseDto> getWarehouseByName(@RequestParam String name) {

    return warehouseService
        .getWarehouseByName(name)
        .map(w -> ResponseEntity.ok(toDto(w)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @GetMapping
  public ResponseEntity<List<WarehouseResponseDto>> getAllWarehouses() {

    List<WarehouseResponseDto> dtos =
        warehouseService.getAllWarehouses().stream().map(WarehouseMapper::toDto).toList();

    return ResponseEntity.ok(dtos);
  }
}
