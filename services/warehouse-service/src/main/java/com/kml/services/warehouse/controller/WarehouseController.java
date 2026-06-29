package com.kml.services.warehouse.controller;

import com.kml.services.common.ApiResponse;
import com.kml.services.common.security.jwt.JwtAuthenticatedUser;
import com.kml.services.warehouse.dto.StorageUnitRequestDto;
import com.kml.services.warehouse.dto.StorageUnitResponseDto;
import com.kml.services.warehouse.dto.WarehouseRequestDto;
import com.kml.services.warehouse.dto.WarehouseResponseDto;
import com.kml.services.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WarehouseResponseDto> createWarehouse(@Valid @RequestBody WarehouseRequestDto request, @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(warehouseService.createWarehouse(request, principal), "Warehouse created");
    }

    @GetMapping("/{id}")
    public ApiResponse<WarehouseResponseDto> getWarehouse(@PathVariable Long id, @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(warehouseService.getWarehouse(id, principal));
    }

    @GetMapping
    public ApiResponse<List<WarehouseResponseDto>> getWarehouses(@RequestParam(required = false) Long ownerUserId, @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(warehouseService.getWarehouses(ownerUserId, principal));
    }

    @GetMapping("/{id}/storage-units")
    public ApiResponse<List<StorageUnitResponseDto>> getStorageUnits(@PathVariable Long id, @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(warehouseService.getStorageUnits(id, principal));
    }

    @PostMapping("/{id}/storage-units")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<StorageUnitResponseDto> addStorageUnit(
        @PathVariable Long id,
        @Valid @RequestBody StorageUnitRequestDto request,
        @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(warehouseService.addStorageUnit(id, request, principal), "Storage unit created");
    }
}
