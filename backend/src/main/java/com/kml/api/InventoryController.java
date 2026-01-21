package com.kml.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kml.capacity.dto.InventoryItemResponseDto;
import com.kml.capacity.dto.QuantityUpdateDto;
import com.kml.capacity.service.InventoryService;
import com.kml.domain.inventory.InventoryItem;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

  private final InventoryService inventoryService;

  public InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  // Create inventory item

  // Update inventory quantity
  @PatchMapping("/{sku}/quantity")
  public ResponseEntity<InventoryItemResponseDto> updateQuantity(
      @PathVariable String sku, @Valid @RequestBody QuantityUpdateDto dto) {
    InventoryItem updatedItem = inventoryService.updateQuantity(sku, dto.getDelta());
    return ResponseEntity.ok(mapToResponseDto(updatedItem));
  }

  // Get All inventories
  @GetMapping
  public ResponseEntity<List<InventoryItemResponseDto>> getAllInventories() {
    List<InventoryItem> inventoryItems = inventoryService.getAllInventories();

    List<InventoryItemResponseDto> dtos =
        inventoryItems.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  // Get inventory by SKU
  @GetMapping("/sku/{sku}")
  public ResponseEntity<InventoryItemResponseDto> getInventoryBySku(@PathVariable String sku) {
    InventoryItem item = inventoryService.getInventoryBySku(sku);
    return ResponseEntity.ok(mapToResponseDto(item));
  }

  // Get inventoryby Id
  @GetMapping("/{id}")
  public ResponseEntity<InventoryItemResponseDto> getInventoryById(@PathVariable Long id) {
    InventoryItem item = inventoryService.getInventoryById(id);
    return ResponseEntity.ok(mapToResponseDto(item));
  }

  // Search inventory by Name
  @GetMapping("/search/by-name")
  public ResponseEntity<List<InventoryItemResponseDto>> getInventoryByName(
      @RequestParam String name) {
    List<InventoryItem> inventoryItems = inventoryService.getInventoryByName(name);
    List<InventoryItemResponseDto> dtos =
        inventoryItems.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  // Filter by Quantity Range
  @GetMapping("/search/by-quantity")
  public ResponseEntity<List<InventoryItemResponseDto>> getInventoryByQuantity(
      @RequestParam int minQuantity, @RequestParam int maxQuantity) {
    List<InventoryItem> inventoryItems =
        inventoryService.getInventoryByRange(minQuantity, maxQuantity);

    List<InventoryItemResponseDto> dtos =
        inventoryItems.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  // Search by Combined Filters (Name + SKU)
  @GetMapping("/search/by-sku-name")
  public ResponseEntity<List<InventoryItemResponseDto>> getInventoryByFilter(
      @RequestParam String sku, @RequestParam String name) {
    List<InventoryItem> inventoryItems = inventoryService.getInventoryByFilter(sku, name);

    List<InventoryItemResponseDto> dtos =
        inventoryItems.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  // Delete - Delete

  // Mapping
  private InventoryItemResponseDto mapToResponseDto(InventoryItem item) {
    InventoryItemResponseDto dto = new InventoryItemResponseDto();
    dto.setId(item.getId());
    dto.setSku(item.getSku());
    dto.setName(item.getName());
    dto.setQuantity(item.getQuantity());
    dto.setCreatedAt(item.getCreatedAt());
    dto.setUpdatedAt(item.getUpdatedAt());

    return dto;
  }
}
