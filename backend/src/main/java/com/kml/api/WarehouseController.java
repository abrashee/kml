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
import org.springframework.web.bind.annotation.RestController;

import com.kml.capacity.dto.WarehouseRequestDto;
import com.kml.capacity.dto.WarehouseResponseDto;
import com.kml.capacity.security.CurrentUserProvider;
import com.kml.capacity.service.WarehouseService;
import com.kml.domain.user.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

  private final WarehouseService warehouseService;
  private final CurrentUserProvider currentUserProvider;

  public WarehouseController(
      WarehouseService warehouseService, CurrentUserProvider currentUserProvider) {
    this.warehouseService = warehouseService;
    this.currentUserProvider = currentUserProvider;
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @PostMapping
  public ResponseEntity<WarehouseResponseDto> createWarehouse(
      @RequestBody @Valid WarehouseRequestDto warehouseRequestDto) {

    User currentUser = currentUserProvider.getCurrentUser();
    WarehouseResponseDto created =
        warehouseService.createWarehouse(
            currentUser, warehouseRequestDto.getName(), warehouseRequestDto.getAddress());

    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  @GetMapping("/{id}")
  public ResponseEntity<WarehouseResponseDto> getWarehouseById(@PathVariable Long id) {

    WarehouseResponseDto dto =
        warehouseService
            .getWarehouseById(id)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));

    return ResponseEntity.ok(dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  @GetMapping
  public ResponseEntity<List<WarehouseResponseDto>> getAllWarehouses() {
    List<WarehouseResponseDto> dtos = warehouseService.getAllWarehouses();
    return ResponseEntity.ok(dtos);
  }
}
