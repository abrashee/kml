package com.kml.services.inventory.service;

import com.kml.services.inventory.dto.InventoryItemRequestDto;
import com.kml.services.inventory.dto.InventoryItemResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {

    InventoryItemResponseDto createInventoryItem(InventoryItemRequestDto request);

    InventoryItemResponseDto adjustQuantity(Long id, int delta);

    InventoryItemResponseDto getInventoryItem(Long id);

    Page<InventoryItemResponseDto> getInventory(String sku, Long warehouseId, Pageable pageable);

    void deleteInventoryItem(Long id);
}
