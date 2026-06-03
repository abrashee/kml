package com.kml.inventory.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kml.inventory.dto.InventoryItemRequestDto;
import com.kml.inventory.dto.InventoryItemResponseDto;
import com.kml.inventory.dto.InventoryQuantityUpdateRequestDto;
import com.kml.inventory.service.InventoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Tag(name = "Inventory")
@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

  private final InventoryService inventoryService;

  public InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @PostMapping
  public ResponseEntity<InventoryItemResponseDto> createInventoryItem(
      @RequestBody @Valid InventoryItemRequestDto requestDto) {

    InventoryItemResponseDto item =
        inventoryService.createInventoryItem(
            requestDto.getSku(), requestDto.getName(), requestDto.getQuantity());

    return ResponseEntity.status(HttpStatus.CREATED).body(item);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @PatchMapping("/{sku}")
  public ResponseEntity<InventoryItemResponseDto> updateQuantity(
      @PathVariable @NotBlank String sku,
      @Valid @RequestBody InventoryQuantityUpdateRequestDto dto) {

    InventoryItemResponseDto updatedItem = inventoryService.updateQuantity(sku, dto.getDelta());
    return ResponseEntity.ok(updatedItem);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping
  public ResponseEntity<Page<InventoryItemResponseDto>> getInventories(
      @RequestParam(required = false) String search,
      @PageableDefault(size = 20) Pageable pageable) {

    Page<InventoryItemResponseDto> items = inventoryService.getInventoriesPage(search, pageable);
    return ResponseEntity.ok(items);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/sku/{sku}")
  public ResponseEntity<InventoryItemResponseDto> getInventoryBySku(
      @PathVariable @NotBlank String sku) {
    InventoryItemResponseDto item = inventoryService.getInventoryBySku(sku);
    return ResponseEntity.ok(item);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}")
  public ResponseEntity<InventoryItemResponseDto> getInventoryById(@PathVariable @Min(1) Long id) {
    InventoryItemResponseDto item = inventoryService.getInventoryById(id);
    return ResponseEntity.ok(item);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteInventory(@PathVariable @Min(1) Long id) {
    inventoryService.deleteInventoryItem(id);
    return ResponseEntity.noContent().build();
  }
}

