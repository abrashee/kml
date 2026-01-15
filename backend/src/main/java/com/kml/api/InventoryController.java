package com.kml.api;

import com.kml.capacity.dto.InventoryItemResponseDto;
import com.kml.capacity.dto.QuantityUpdateDto;
import com.kml.capacity.service.InventoryService;
import com.kml.domain.inventory.InventoryItem;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

  private final InventoryService inventoryService;

  public InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  // Create - Post

  // Update - Put

  // Update - Partial - Patch
  @PatchMapping("/{sku}/quantity")
  public ResponseEntity<InventoryItemResponseDto> updateQuantity(
      @PathVariable String sku, @Validated @RequestBody QuantityUpdateDto dto) {
    InventoryItem updatedItem = inventoryService.updateQuantity(sku, dto.getDelta());
    return ResponseEntity.ok(mapToResponseDto(updatedItem));
  }

  // Get All - Get
  @GetMapping
  public ResponseEntity<List<InventoryItemResponseDto>> getAllInventories() {
    List<InventoryItem> inventoryItems = inventoryService.getAllInventories();

    List<InventoryItemResponseDto> dtos =
        inventoryItems.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  // Get by SKU - Get
  @GetMapping("/{sku}")
  public ResponseEntity<InventoryItemResponseDto> getInventoryBySku(@PathVariable String sku) {
    InventoryItem item = inventoryService.getInventoryBySku(sku);

    return ResponseEntity.ok(mapToResponseDto(item));
  }

  // Get by Id - Get
  @GetMapping("{id}")
  public ResponseEntity<InventoryItemResponseDto> getInventoryById(@PathVariable Long id) {
    InventoryItem item = inventoryService.getInventoryById(id);

    return ResponseEntity.ok(mapToResponseDto(item));
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
