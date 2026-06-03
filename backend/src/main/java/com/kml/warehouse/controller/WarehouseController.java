package com.kml.warehouse.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.kml.security.CurrentUserProvider;
import com.kml.user.entity.User;
import com.kml.warehouse.dto.WarehouseRequestDto;
import com.kml.warehouse.dto.WarehouseResponseDto;
import com.kml.warehouse.service.WarehouseService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Warehouses")
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
  public ResponseEntity<Page<WarehouseResponseDto>> getAllWarehouses(
      @RequestParam(required = false) String search,
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(warehouseService.getAllWarehousesPage(search, pageable));
  }
}

