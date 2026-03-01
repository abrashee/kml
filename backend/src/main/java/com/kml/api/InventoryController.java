package com.kml.api;

import com.kml.capacity.dto.InventoryItemRequestDto;
import com.kml.capacity.dto.InventoryItemResponseDto;
import com.kml.capacity.dto.QuantityUpdateDto;
import com.kml.capacity.service.InventoryService;
import jakarta.validation.Valid;
import java.util.List;
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
  @PatchMapping("/{sku}/quantity")
  public ResponseEntity<InventoryItemResponseDto> updateQuantity(
      @PathVariable String sku, @Valid @RequestBody QuantityUpdateDto dto) {
    InventoryItemResponseDto updatedItem = inventoryService.updateQuantity(sku, dto.getDelta());
    return ResponseEntity.ok(updatedItem);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping
  public ResponseEntity<List<InventoryItemResponseDto>> getAllInventories() {
    List<InventoryItemResponseDto> inventoryItems = inventoryService.getAllInventories();
    return ResponseEntity.ok(inventoryItems);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/sku/{sku}")
  public ResponseEntity<InventoryItemResponseDto> getInventoryBySku(@PathVariable String sku) {
    InventoryItemResponseDto item = inventoryService.getInventoryBySku(sku);
    return ResponseEntity.ok(item);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}")
  public ResponseEntity<InventoryItemResponseDto> getInventoryById(@PathVariable Long id) {
    InventoryItemResponseDto item = inventoryService.getInventoryById(id);
    return ResponseEntity.ok(item);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/search/by-name")
  public ResponseEntity<List<InventoryItemResponseDto>> getInventoryByName(
      @RequestParam String name) {
    List<InventoryItemResponseDto> inventoryItems = inventoryService.getInventoryByName(name);
    return ResponseEntity.ok(inventoryItems);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/search/by-quantity")
  public ResponseEntity<List<InventoryItemResponseDto>> getInventoryByQuantity(
      @RequestParam int minQuantity, @RequestParam int maxQuantity) {
    List<InventoryItemResponseDto> inventoryItems =
        inventoryService.getInventoryByRange(minQuantity, maxQuantity);
    return ResponseEntity.ok(inventoryItems);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/search/by-sku-name")
  public ResponseEntity<List<InventoryItemResponseDto>> getInventoryByFilter(
      @RequestParam String sku, @RequestParam String name) {
    List<InventoryItemResponseDto> inventoryItems =
        inventoryService.getInventoryByFilter(sku, name);
    return ResponseEntity.ok(inventoryItems);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
    inventoryService.deleteInventoryItem(id);
    return ResponseEntity.noContent().build();
  }
}
