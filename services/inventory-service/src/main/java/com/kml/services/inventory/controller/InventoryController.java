package com.kml.services.inventory.controller;

import com.kml.services.common.ApiResponse;
import com.kml.services.inventory.dto.InventoryItemRequestDto;
import com.kml.services.inventory.dto.InventoryItemResponseDto;
import com.kml.services.inventory.dto.InventoryQuantityUpdateRequestDto;
import com.kml.services.inventory.service.InventoryService;
import jakarta.validation.Valid;
import java.util.List;
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
    public ApiResponse<InventoryItemResponseDto> createInventory(@Valid @RequestBody InventoryItemRequestDto request) {
        return ApiResponse.ok(inventoryService.createInventoryItem(request), "Inventory item created");
    }

    @GetMapping("/{id}")
    public ApiResponse<InventoryItemResponseDto> getInventory(@PathVariable Long id) {
        return ApiResponse.ok(inventoryService.getInventoryItem(id));
    }

    @GetMapping
    public ApiResponse<List<InventoryItemResponseDto>> getInventory(
        @RequestParam(required = false) String sku,
        @RequestParam(required = false) Long warehouseId) {
        return ApiResponse.ok(inventoryService.getInventory(sku, warehouseId));
    }

    @PatchMapping("/{id}/quantity")
    public ApiResponse<InventoryItemResponseDto> adjustQuantity(
        @PathVariable Long id,
        @RequestBody InventoryQuantityUpdateRequestDto request) {
        return ApiResponse.ok(inventoryService.adjustQuantity(id, request.delta()), "Inventory quantity updated");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventoryItem(id);
    }
}
