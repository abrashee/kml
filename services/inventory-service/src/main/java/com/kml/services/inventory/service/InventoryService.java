package com.kml.services.inventory.service;

import com.kml.services.inventory.dto.InventoryItemRequestDto;
import com.kml.services.inventory.dto.InventoryItemResponseDto;
import java.util.List;

public interface InventoryService {

    InventoryItemResponseDto createInventoryItem(InventoryItemRequestDto request);

    InventoryItemResponseDto adjustQuantity(Long id, int delta);

    InventoryItemResponseDto getInventoryItem(Long id);

    List<InventoryItemResponseDto> getInventory(String sku, Long warehouseId);

    void deleteInventoryItem(Long id);
}
