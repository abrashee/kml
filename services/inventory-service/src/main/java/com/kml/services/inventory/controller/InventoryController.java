package com.kml.services.inventory.controller;

import com.kml.services.common.ApiResponse;
import com.kml.services.common.security.jwt.JwtAuthenticatedUser;
import com.kml.services.inventory.dto.InventoryItemRequestDto;
import com.kml.services.inventory.dto.InventoryItemResponseDto;
import com.kml.services.inventory.dto.InventoryQuantityUpdateRequestDto;
import com.kml.services.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InventoryItemResponseDto> createInventory(@Valid @RequestBody InventoryItemRequestDto request, @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(inventoryService.createInventoryItem(request, principal), "Inventory item created");
    }

    @GetMapping("/{id}")
    public ApiResponse<InventoryItemResponseDto> getInventory(@PathVariable Long id, @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(inventoryService.getInventoryItem(id, principal));
    }

    @GetMapping
    public ApiResponse<Page<InventoryItemResponseDto>> getInventory(
        @RequestParam(required = false) String sku,
        @RequestParam(required = false) Long warehouseId,
        Pageable pageable,
        @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(inventoryService.getInventory(sku, warehouseId, pageable, principal));
    }

    @PatchMapping("/{id}/quantity")
    public ApiResponse<InventoryItemResponseDto> adjustQuantity(
        @PathVariable Long id,
        @Valid @RequestBody InventoryQuantityUpdateRequestDto request,
        @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        return ApiResponse.ok(inventoryService.adjustQuantity(id, request.delta(), principal), "Inventory quantity updated");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInventory(@PathVariable Long id, @AuthenticationPrincipal JwtAuthenticatedUser principal) {
        inventoryService.deleteInventoryItem(id, principal);
    }
}
