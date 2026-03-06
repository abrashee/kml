package com.kml.api;

import com.kml.capacity.dto.InventoryItemRequestDto;
import com.kml.capacity.dto.InventoryItemResponseDto;
import com.kml.capacity.dto.InventoryQuantityUpdateRequestDto;
import com.kml.capacity.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
  @PatchMapping("/{sku}")
  public ResponseEntity<InventoryItemResponseDto> updateQuantity(
      @PathVariable @NotBlank String sku,
      @Valid @RequestBody InventoryQuantityUpdateRequestDto dto) {

    InventoryItemResponseDto updatedItem = inventoryService.updateQuantity(sku, dto.getDelta());
    return ResponseEntity.ok(updatedItem);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping
  public ResponseEntity<List<InventoryItemResponseDto>> getInventories(
      @RequestParam(required = false) String sku,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) @Min(0) Integer minQuantity,
      @RequestParam(required = false) @Min(0) Integer maxQuantity) {

    List<InventoryItemResponseDto> inventoryItems =
        inventoryService.getInventoriesFiltered(sku, name, minQuantity, maxQuantity);

    return ResponseEntity.ok(inventoryItems);
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
